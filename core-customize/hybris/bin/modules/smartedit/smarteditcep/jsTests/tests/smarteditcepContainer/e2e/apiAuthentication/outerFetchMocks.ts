
import * as fetchMock from 'fetch-mock';

fetchMock.mock('path:/smartedit/settings', {
	"bootstrapOuterExtensions": [{
		smarteditcepContainer: "/web/webroot/smarteditcep/js/smarteditcepContainer.js"
	}],
	'smartedit.sso.enabled': 'true'
});

const oauthToken0 = {
	access_token: 'access-token0',
	token_type: 'bearer'
};

// through SSO
fetchMock.post('path:/smarteditcep/authenticate', function(url, opts) {
	const query = JSON.parse(opts.body as string);
	if (
		query.client_id === 'smartedit' &&
		sessionStorage.getItem('sso.authenticate.failure') !== 'true'
	) {
		return Promise.resolve(oauthToken0);
	} else {
		return Promise.reject({
			error_description: 'SSO authentication issue'
		});
	}
});