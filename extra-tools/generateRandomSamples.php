<?php

$PARTITION_PERCENT = 67;

$myfile = fopen("datasource_ids.json", "r") or die("Unable to open file!");
$jsonTxt =  fread($myfile,filesize("datasource_ids.json"));
$jsonObj = json_decode($jsonTxt, JSON_PRETTY_PRINT);
fclose($myfile);

$trainingIds = [];
$testingIds = [];

foreach ($jsonObj  as $key => $value) {
	echo "$key\n";
	$amountOfIds = count($value);
	$amountOfIdsInTrain = round((67/100 * $amountOfIds));
	$amountOfIdsInTest = $amountOfIds - $amountOfIdsInTrain;
	echo "Total: $amountOfIds\n";
	echo "$amountOfIdsInTrain of ids in train\n";
	echo "$amountOfIdsInTest of ids in test\n\n";
	for($i=0;$i<$amountOfIds;$i++){
		if($amountOfIdsInTest >0){
			if($i % 2 == 0){
				$trainingIds[] =  $value[$i];
			}else{
				$testingIds[] =  $value[$i];
				$amountOfIdsInTest--;
			}
		}else{
				$trainingIds[] =  $value[$i];
		}
	}//for
}//foreach
echo "TRAINING: (";
echo implode(",", $trainingIds);
///print_r($trainingIds);
echo "); \n";
echo "TESTING: (";
echo implode(",", $testingIds);
//print_r($testingIds);
echo "); \n"
    
?>
