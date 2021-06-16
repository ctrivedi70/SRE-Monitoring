// Below methods are used to build and test the java code with Maven
def buildMethod() {
    println('mavenBuildMethod enter');
    sh('mvn clean install -DskipTests package');
    
    println('mavenBuildMethod exit');
}

def testMethod() {
    println('mavenTestMethod enter');
    sh('mvn test');
    println('mavenTestMethod exit');
}

def sonarMethod() {
	println('Sonar Method enter');
    def scannerHome = tool 'Sonar Scanner';

     sh "${scannerHome}/bin/sonar-scanner -Dsonar.login=$USERNAME -Dsonar.password=$PASSWORD";
	println('Sonar Method exit');
	
}

return this // Its important to return after all the functions.



