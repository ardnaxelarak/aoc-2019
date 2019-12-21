class="$1"
rest="${@:2}"
mvn exec:java -Dexec.mainClass="$class" -Dexec.args="$rest"
