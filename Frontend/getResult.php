<?php
	$servername = "";
	$username = "";
	$password = "";
	$dbname = "";
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 
	
	$name = mysqli_real_escape_string($conn, $_POST['query']);
	 $user_query = explode(" ",$name);
	 
	 $query_len=count($user_query);
	
	if($query_len == 2)	
		{
			/*$sql='SELECT t1.*,web_pages.url,web_pages.text,web_pages.html from (SELECT result1.docid,(result1.TF_IDF + result2.TF_IDF) as total_tfidf 
				FROM (SELECT * FROM inverted_index WHERE term = "'.$user_query[0].'") AS result1 
			INNER JOIN (SELECT * FROM inverted_index WHERE term = "'.$user_query[1].'" ) AS result2 
			ON result1.docid = result2.docid) as t1 INNER JOIN web_pages ON t1.docid = web_pages.docid
			ORDER BY total_tfidf DESC';*/
			$sql='SELECT t1.*,web_pages.url,web_pages.text,web_pages.html,web_pages.pagerank,web_pages.title,web_pages.anchor FROM (SELECT result1.docid,sqrt(power(result1.TF_IDF,2) + pow(result2.TF_IDF,2)) as total_tfidf 
				FROM (
				SELECT * FROM inverted_index WHERE term = "'.$user_query[0].'"
				) AS result1 
				INNER JOIN (
				SELECT * FROM inverted_index WHERE term = "'.$user_query[1].'" 
				) AS result2 ON result1.docid = result2.docid) as t1 INNER JOIN web_pages ON t1.docid = web_pages.docid 
				ORDER BY total_tfidf DESC';
		}
	else
		{
			$sql='Select t1.TF_IDF as total_tfidf,web_pages.url,web_pages.text,web_pages.html,web_pages.pagerank,web_pages.title,web_pages.anchor from (Select * from inverted_index WHERE term = "'.$name.'") as t1 INNER JOIN web_pages ON t1.docid = web_pages.docid ORDER BY TF_IDF DESC';
		}
	//$sql="SELECT * FROM ir_db.inverted_index LIMIT 100 ";
	$result = $conn->query($sql);

	
/*if (!$result->num_rows > 0) {
    echo "0 results";
}*/
?>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Search</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <link rel="stylesheet" href="./bootstrap.css" media="screen">
    <link rel="stylesheet" href="../assets/css/custom.min.css">
    
    <style>
		.result{
		width:80%;
		}
		.contain {
		border:solid 1px #eaeaea;
		background-color:#f6f6f6;
		}
		.searchbar{
		list-style:none;
		
		
		}
        #search-text-input{
            border-top:thin solid  #e5e5e5;
            border-right:thin solid #e5e5e5;
            border-bottom:thin solid #e5e5e5;
            border-left:thin solid  #e5e5e5;
        
			float=left;
            height:40px;
            outline:0;
            padding:.4em 0 .4em .4em; 
            width:60%; 
        }
         
      
         
        #button-holder img{
			
            height:40px; 
        }
		
		
		

        </style>
  </head>
  <body style="text-align:justify;">
   

    <!--<div class="container">-->
		<div class=contain >
			<ul >
			
				
			<li class="searchbar" >
			<span style="font-size: 3em;padding-right:.2em;"><font color="#2196f3">S</font><font color="#F90101">e</font><font color="#F2B50F">a</font><font color="#2196f3">r</font><font color="#F90101">c</font><font color="#00933B">h</font></span>	
			</li>
			<li class="searchbar" >
				<form name="search_form" action="getResult.php" method="POST">			
					<input name="query" type="text" placeholder="Search it" style="float:left" id="search-text-input" value="<?php echo $name?>" ></input>
				
				<li  id='button-holder' >
					<submit name="submit" id="submit"><img src='search.png' /></submit>
				</li>
					</form>
			</li>
			</ul>				
	   </div>
	<!--container ends-->
	
	<div id=result class=result name=result style="margin-left:1em;margin-top:1em;">
		<ul style="list-style-type:none;">
		
<?PHP
/*$sqlmaxrank="SELECT MAX(rank) as rank FROM pagerank LIMIT 1";
$maxrank_result = $conn->query($sqlmaxrank);
$maxrankvalue = $maxrank_result->fetch_assoc();*/
//initialize weights
$t=0;$pos=0;$s=0;//ranking parameters!!!!
//flags to check whether query terms occur in the title

function get_title($html) {
  return preg_match('!<title>(.*?)</title>!i', $html, $matches) ? $matches[1] : '';
}
function check_position($term1,$term2,$text){
		//if($query_len==2)
		{	if (preg_match("/\b".$term1." ".$term2."\b/i", $text))
				{//echo "Higher Rank PLEASE!!!!!!!!!!";
					return true; //weight for this in rank
				}
		}
}

function in_title($terms,$title) {

	if (preg_match("/\b".$terms."\b/i", $title)) {
		//echo "<br>in title :true";
		return true;
	}
	else 
	{
		//echo "<br>FALSE why???</br>";
		return false;
	}
}
function in_url($term,$url){
if (preg_match("/".$term."/i", $url)) {
		//echo "<br>in title :true";
		return true;
	}
	else 
	{
		//echo "<br>FALSE why???</br>";
		return false;
	}
}
function remove_http($url) {
   $disallowed = array('http://', 'https://');
   foreach($disallowed as $d) {
      if(strpos($url, $d) === 0) {
         return str_replace($d, '', $url);
      }
   }
   return $url;
}
function url_check($url1,$url2){
	
if (preg_match("/\b".remove_http($url1)."\b/i", $url2)) {
		//echo "<br>in title :true";
		return true;
	}
	else 
	{
		//echo "<br>FALSE why???</br>";
		return false;
	}
}
function print_snippet($text){
$snippet2=preg_replace("/[^A-Za-z0-9]+/"," ",$text);
	global $user_query,$name,$query_len;
				$snippet=explode(" ",$snippet2);
				$snip_len=count($snippet);
				$flag1=0;
				$flaq2=0;
				$key1=-1;
				$key2=-1;
				for($j=0;$j<$snip_len;$j++)
				{
					$key1++;
					if((strcasecmp($snippet[$j], $user_query[0])==0) &&($key1!=0))
					{
					//echo "<br>".$key1.'<br>';
						$flag1=1;
						break;
					
					}
				}
				//echo '<br>';
				if($query_len==2)
				{
					for($k=0;$k<$snip_len;$k++)
					{
						$key2++;
						if((strcasecmp($snippet[$k],$user_query[1])==0) &&($key2!=0)&&($key2>$key1))
						{
							$flag2=1;
							break;
						}
					}
					//for 2 word search
					//if($query_len==2)
					{
						if($flag1==1 && $flag2==1) 
						{
								if($key1>20)
									$t_start=$key1-20;
								else
									$t_start=0;
								//echo '<u>'.$snippet[$key1].' '. $key1.'</u>';
								if($key1>$snip_len-21)
									$t_end=$snip_len-1;
								else
									$t_end=$key1+20;
								for( $i=$t_start;$i<=$t_end;$i++)
								{
									if((strcasecmp($snippet[$i],$user_query[0])==0)||(strcasecmp($snippet[$i],$user_query[1])==0))
										echo "<b> ".$snippet[$i]." </b>";
									else	
										echo " ".$snippet[$i]." ";
								}
								echo "<b> <font color='#ff0000'>....</font></b>";
								if($key2>20)
									$t_start=$key2-20;
								else
									$t_start=0;
							//echo '<u>'.$snippet[$key1].' '. $key1.'</u>';
								if($key2>$snip_len-21)
									$t_end=$snip_len-1;
								else
									$t_end=$key2+20;
							
								for( $i=$t_start;$i<$t_end;$i++)
									{
										if((strcasecmp($snippet[$i],$user_query[0])==0)||strcasecmp($snippet[$i],$user_query[1])==0)
											echo "<b> ".$snippet[$i]." </b>";
										else	
											echo  " ".$snippet[$i]." ";
									}
							
						}
						else if($flag1==1)
						{
							if($key1>20)
								$t_start=$key1-20;
							else
								$t_start=0;
							//echo '<u>'.$snippet[$key1].' '. $key1.'</u>';
							if($key1>$snip_len-21)
								$t_end=$snip_len-1;
							else
								$t_end=$key1+20;
						
							for( $i=$t_start;$i<$t_end;$i++)
								{
									if(strcasecmp($snippet[$i],$user_query[0])==0)
										echo "<b> ".$snippet[$i]." </b>";
									else	
										echo  " ".$snippet[$i]." ";
								}
						}
						else if($flag2==1)
						{
							if($key2>20)
								$t_start=$key2-20;
							else
								$t_start=0;
							//echo '<u>'.$snippet[$key1].' '. $key1.'</u>';
							if($key2>$snip_len-21)
								$t_end=$snip_len-1;
							else
								$t_end=$key2+20;
						
							for( $i=$t_start;$i<$t_end;$i++)
								{
									if(strcasecmp($snippet[$i],$user_query[1])==0)
										echo "<b> ".$snippet[$i]." </b>";
									else	
										echo  " ".$snippet[$i]." ";
								}
						}
					}
				}
				//for 1 word search
				else
				{
					if($flag1==1)
					{
						if($key1>20)
							$t_start=$key1-20;
						else
							$t_start=0;
					//echo '<u>'.$snippet[$key1].' '. $key1.'</u>';
						if($key1>$snip_len-21)
							$t_end=$snip_len-1;
						else
							$t_end=$key1+20;
					
					for( $i=$t_start;$i<$t_end;$i++)
							{
								if(strcasecmp($snippet[$i],$name)==0)
									echo "<b> ".$snippet[$i]." </b>";
								else	
									echo  " ".$snippet[$i]." ";
							}
					
					}
					
				}

}
	// output of tfidf query
	if ($result->num_rows > 0) {
    // output data of each row
		while($row = $result->fetch_assoc()) {
			$url=remove_http($row['url']);
			if((strpos($url,"informatics.uci.edu") !== FALSE)||(strpos($url,"mailman") !== FALSE)||(strpos($url,"archive") !== FALSE)||(strpos($url,"drzaius") !== FALSE)||(strpos($url,"fano.ics") !== FALSE)||(stripos($url,"publications") !== FALSE)||(stripos($url,"genomics.uci") !== FALSE)||(stripos($url,"sprout.ics") !== FALSE))
			{
				continue;
			}
			
			
			$flag_t1=0;$flag_t2=0;$t=0;$u=0;$i=0;$a=0;
			/*$sqlrank='SELECT rank from ir_db.pagerank WHERE url="'.$row['url'].'" limit 1';
			
			
			$rank_result = $conn->query($sqlrank);
			if ($rank_result->num_rows > 0)
			$rankvalue = $rank_result->fetch_assoc();
			
			$normalized_page_rank=($rankvalue['rank'] / $maxrankvalue['rank']); // noramlize page rank*/
			$p_rank=$row['pagerank'];
			$title1=$row['title'];
			$title=strtolower($title1); // get title of the page
			$clean_title=preg_replace("/[^A-Za-z0-9]+/"," ",$title);
			$title_token=explode(" ",$clean_title);
			$title_size=count($title_token);
			//echo $title_size;
			
			
				if(in_title($user_query[0],$title))//check whether query in title
				{	
					$flag_t1=1;
					$t=1;///$title_size;
				}
			
			if($query_len==2) //check if terms in the query occur as it is in the document
			{	
					
				$tfidf=$row['total_tfidf'];
				
				if(check_position($user_query[0],$user_query[1],$row['text']))
				{
					$pos=1;
				}
				$t=0.3/$title_size;
				if(in_title($user_query[1],$title)) // to check whether second term of the search is in the title 
				{	
					$flag_t2=1;
					
					if($flag_t1==1)
					{
						$t=0.5+1/$title_size;
					//add weight;
					}
				}
			}
			//check anchor text from its parent
			
			
			/*if($query_len==1&&$flag_t1==1) // if its a one word query
					{	//echo"4";
					$t=1;
						//echo "single term is in title!!!";
					}
			*/
			
			//echo "rank: ".$rankvalue['rank']." normalized rank: ".$normalized_page_rank."<br>";
			//echo "<b> pr: ".$normalized_page_rank." -- title weigt: ".$t." -- position: ".$pos."</b>";
			//echo "title: ".$title."<br>";
			//echo "<b>normalized_page_rank ".$normalized_page_rank." title weight ".$t." pos rank: ".$pos."</b><br>";
			//$rank_array[$row['url']]=(0.3*$normalized_page_rank)+(0.45*$t)+(0.05*$pos);
			
			$anchor=$row['anchor'];
			
			
			
			if(strpos($url, "index") !== FALSE) // url contains index.php or index.html
			{
					$i=1;
			}
			if(stripos($url,"repository") !==FALSE) //if url contains word repository
			{
				$u=1;
			}
			if($query_len==2)
			{
				if((stripos($anchor,$user_query[0]) !== FALSE) ||(stripos($anchor,$user_query[1]) !== FALSE )) // if query terms in anchor tag
				{
						//echo "anchor2: ".$anchor;
						$a=1;
				}
				if(stripos($url, $user_query[0]) !== FALSE||stripos($url, $user_query[1]) !== FALSE) //check if query terms occur in query
				{
				//echo "<br>".$url."query term found in url @@@	";
					if($i==1)   // if query term in url and index page in that url
					{
						$u=0.7;	
					}
					if(stripos($url, $user_query[0]) !== FALSE&&stripos($url, $user_query[1]) !== FALSE) //both terms in url
					{
						$u=0.9;
						if($i==1)   // if query term in url and index page in that url
						{
						$u=1;	
						}
					}
						
					if(stripos($url,"repository") !==FALSE) //if url contains word repository
						{
							$u=1;
						}
					if(stripos($url,"~".$user_query[0]) !== FALSE ||stripos($url,"~".$user_query[1]) !== FALSE )  //for prof having ~ in their homepage generally
					{
						if($i==1)   // if index page of any teacher
							{
								$u=0.9;
							}
						else // contains ~ but not index page
						{
							$u=0.8;
						}
						if(stripos($url,"repository") !==FALSE) //if url contains word repository
								{
									$u=1;
								}
						
						
					}
					else // if no ~
					{
						$u=0.5;
						if(stripos($url,"repository") !==FALSE) //if url contains word repository
								{
									$u=1;
								}
					}
				}
				if((stripos($url,"publications") !== FALSE)) //if url contains publication
						{
							$u=0.1;
							
						}
			}
			elseif(strpos($url, $user_query[0]) !== FALSE)
			{
				//echo "<br>".$url."query term found in url";
				$u=0.5;
				if(stripos($url,"repository") !==FALSE)
				{
					$u=1;
				}
				if(strpos($anchor, $user_query[0]) !== FALSE)
				{
					//echo "anchor1: ".$anchor;
					$a=1;
				}
			}
			
			
			$rank_array[$url]=0;
			$anchor_rank[$url]=$a;
			$indexpage_rank[$url]=$i;
			$url_rank[$url]=$u;
			$title_rank[$url]=$t;
			$page_rank[$url]=$p_rank;
			$pos_rank[$url]=$pos;
			$tfidf_rank[$url]=$row['total_tfidf'];
			$text_array[$url]=$row['text'];
			$title_array[$url]=$row['title'];
			
			
	}
}
else
{
	echo "0 results";
}
	//sort the tfidf rank array to find max tfidf to normalize
	//function sort_tfidf()
		arsort($page_rank);
		$max_page_rank=array_values($page_rank)[0];
		foreach($page_rank as $p => $p_value) {
		 //echo "URL=" . $t . ", RANK= " . $t_value;
		 $page_rank[$p]=$p_value/$max_page_rank;
		 
		 //echo "modified tfidf: ------ ".($t_value/$max_tfidf)."<br>";
		 }
		arsort($tfidf_rank);
		$max_tfidf=array_values($tfidf_rank)[0];
		//echo "max tfidf:   ->>>>>>>".$max_tfidf;
		foreach($tfidf_rank as $t => $t_value) {
		 //echo "URL=" . $t . ", RANK= " . $t_value;
		 $tfidf_rank[$t]=$t_value/$max_tfidf;
		 $rank_array[$t]=(0.325*($t_value/$max_tfidf))+(0.25*$title_rank[$t])+(0.225*$page_rank[$t])+(0.1*$url_rank[$t])+(0.05*$anchor_rank[$t])+(0.025*$indexpage_rank[$t])+(0.025*$pos_rank[$t]);
		 //echo "modified tfidf: ------ ".($t_value/$max_tfidf)."<br>";
		 }
	/*foreach($tfidf_rank as $x => $x_value) {
     echo "URL=" . $x . ", RANK= " . $x_value;
     echo "<br>";
	 }*/
	//sort according to ranking parameters
	arsort($rank_array);
	//traversing the sorted array and displaying results
	foreach($rank_array as $x => $x_value) {
    // echo "URL=" . $x . ", RANK= " . $x_value;
    // echo "<br>";
	 /*$sorted_sql="SELECT url,html,text,title FROM ir_db.web_pages WHERE url='". $x ."' LIMIT 1";
	 $sorted_result=$conn->query($sorted_sql);
	 
		 $row1 = $sorted_result->fetch_assoc();*/
		 {
				echo '<li><h5><a href=http://"'.$x.'" style=color:blue>'.$title_array[$x].'</a></h5> 
							<b style="color:blue;"><a style="color:green;"target="_blank" href="http://'.$x.'">http://'.$x.'</a></b><br>';
						
					
					print_snippet($text_array[$x]); //call function to print text snippet
					echo'</li>';
					echo '<hr style="width:100%;">';
	
		}
	}
	
	?>	
	</ul>
	</div>
	
	<?php
	mysqli_close($conn);
	?>
   
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="../bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="../assets/js/custom.js"></script>
	
  </body>
  
</html>	