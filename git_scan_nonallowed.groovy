// Below methods are used to scan the git code repo.
def nonAllowedPattern() {
        sh '''
        git secrets --add '[^a-zA-Z\\s+]password\\s*(=|:)\\s*.+',
		git secrets --add 'credential\\s*(=|:)\\s*.+'
		git secrets --add '^key\\s*(=|:)\\s*.+'
		git secrets --add 'userid\\s*(=|:)\\s*.+'
		git secrets --add 'access\\s*(=|:)\\s*.+'
		git secrets --add 'secret\\s*(=|:)\\s*.+'
		git secrets --add 'https?:[a-zA-Z0-9!@#$&()\\-`.+,/\"\\S]+:[a-zA-Z0-9!@#$&()\\-`.+,/\"\\S]+@[a-zA-Z0-9\\-`.+,\\S]+'
		
		'''
}

return this // this is important to return