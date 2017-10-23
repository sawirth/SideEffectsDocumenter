$java = 'C:\Program Files\Java\jdk1.8.0_20\bin\java'
$jar = 'C:\Users\Sandro\Documents\GitHub\SideEffectsDocumenter\build\libs\SideEffects-documenter-all-1.0.jar'
$main = 'main.Main'

# Purano result path
$puranoPath = 'C:\Users\Sandro\Documents\GitHub\slf4j\Purano-Result.json'

# root of -java files
$javaRoot = 'C:\Users\Sandro\Documents\GitHub\slf4j\'


& $java -jar $jar $javaRoot -p $puranoPath