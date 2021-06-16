// Below methods are used to scan the git code repo.
def AllowedPattern() {
        sh '''
        git secrets --add --allowed --literal 'ex@mplepassword'
		git secrets --add --allowed --literal '^$'
		git secrets --add --allowed --literal 'sooper secret'
		git secrets --add --allowed --literal 'process.env.MYSQL_ENV_MYSQL_ROOT_PASSWORD'
		'''
}

return this // this is important to return