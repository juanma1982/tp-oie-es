<?php

$mysqli = new mysqli('127.0.0.1', 'root', 'password', 'spanish_corpus');
$mysqli->set_charset("utf8");
//echo 'Conectado satisfactoriamente';


//Select of the Training partition of the dataSet
$SQL_SELECT_TEXT = "select * from spanish_corpus.re_articulo where id in
(2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,53,55,57,59,62,64,77,83,86,88,90,145,148,150,152,154,156,158,160,162,164,166,169,171,173,175,177,102,105,108,110,113,115,118,120,122,124,127,129);";


$texts = $mysqli->query($SQL_SELECT_TEXT);
$array = array();
while($f = $texts->fetch_object()){
	echo "$f->id\t";
	if(substr("$f->body", -1) == "\n"){
		echo "$f->body";
	}else{
		echo "$f->body\n";
	}
    
    $relations = $mysqli->query("SELECT `re_extraccion_manual`.`id`,
    `re_extraccion_manual`.`entidad01`,
    `re_extraccion_manual`.`relacion`,
    `re_extraccion_manual`.`entidad02`  
FROM `re_extraccion_manual` where id_articulo=".$f->id);

    while($relf = $relations->fetch_object()){        
        echo ("\t($relf->entidad01; $relf->relacion; $relf->entidad02)\n");
    }
    echo "\n";
}
    
?>
