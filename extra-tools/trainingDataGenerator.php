<?php

$mysqli = new mysqli('127.0.0.1', 'root', 'password', 'spanish_corpus');
$mysqli->set_charset("utf8");
//echo 'Conectado satisfactoriamente';


//Select of the Training partition of the dataSet
$SQL_SELECT_TEXT = "select A.id, A.body from re_articulo A where A.id in
(1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,34,35,37,38,39,40,41,42,43,44,45,46,47,48,49,50,52,54,56,58,61,63,65,66,67,68,69,70,73,76,80,85,87,89,91,93,94,98,144,146,149,151,153,155,157,159,161,163,165,167,170,172,174,176,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,101,104,106,109,112,114,116,119,121,123,126,128,130,131,132,134,135,136,137,138,139,142,143); 
";


$texts = $mysqli->query($SQL_SELECT_TEXT);
$array = array();
while($f = $texts->fetch_object()){
    $examples = array("sentence" => $f->body);
    $relArray = array();
    $relations = $mysqli->query("SELECT `re_extraccion_manual`.`id`,
    `re_extraccion_manual`.`entidad01`,
    `re_extraccion_manual`.`relacion`,
    `re_extraccion_manual`.`entidad02`  
FROM `re_extraccion_manual` where id_articulo=".$f->id);

    while($relf = $relations->fetch_object()){
        $relArray[] = array("entity1" => $relf->entidad01 , "relation"=> $relf->relacion,"entity2"=> $relf->entidad02);
    }
    $examples["relations"] = $relArray;
    $array [] =  $examples;
}
$json=array("lang" => "es",
"examples" => $array);


echo json_encode($json, JSON_PRETTY_PRINT);
    
?>
