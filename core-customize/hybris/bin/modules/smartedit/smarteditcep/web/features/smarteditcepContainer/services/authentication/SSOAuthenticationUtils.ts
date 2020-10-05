import {IAuthToken} from 'smarteditcommons';
import {
	CHILD_SMARTEDIT_SENDING_AUTH_ERROR,
	CHILD_SMARTEDIT_SENDING_AUTHTOKEN,
	SSO_CLIENT_ID,
	SSO_DIALOG_MARKER,
	SSO_DIALOG_WINDOW,
	SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT
} from './ssoAuthenticationConstants';

export class SSOAuthenticationUtils {
    /*
	 * case of the App being a popup only meant for authentication and spun up by the main app
     */
	isSSODialog(): boolean {
		return (
			this.window.name === SSO_DIALOG_WINDOW &&
			new RegExp(`[?&]${SSO_DIALOG_MARKER}`).test(location.search)
		);
	}

    /*
     * SSO happens in a popup window launched by AuthenticationHelper#launchSSODialog().
     * Once SSO is successful, a 'LoginToken' cookie is present, this is a pre-requisite for doing a POST to the /authenticate
     * endpoint that will return the Authorization bearer token.
     * This bearer is then sent with postMessage to the opener window, i.e. the SmartEdit application that will resume the pending 401 request.
     */
	completeDialog(): Promise<void> {
		return new Promise<void>((resolve, reject) => {
			let fetchResponse: Response;
			this.window.fetch(SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT, {
				method: 'POST',
				body: JSON.stringify({client_id: SSO_CLIENT_ID}),
				headers: {
					'Content-Type': 'application/json'
				}
			}).then((response: Response) => {
				fetchResponse = response;
				return response.json();
			})
				.then((authToken: IAuthToken) => {
					this.window.opener.postMessage(
						{
							eventId: CHILD_SMARTEDIT_SENDING_AUTHTOKEN,
							authToken
						},
						this.document.location.origin
					);
					this.window.close();
					resolve();
				}, (errorResponse: any) => {
					const clonableHttpErrorResponse = {
						data: errorResponse,
						status: fetchResponse ? fetchResponse.status : errorResponse ? errorResponse.status : ''
					};
					this.window.opener.postMessage(
						{
							eventId: CHILD_SMARTEDIT_SENDING_AUTH_ERROR,
							error: clonableHttpErrorResponse
						},
						this.document.location.origin
					);
					this.window.close();
					reject();
				});

		});
	}

	getWindow(): Window {
		return window;
	}

	private get window() {
		return this.getWindow();
	}

	private get document(): Document {
		return this.window.document;
	}
}

export const ssoAuthenticationUtils = new SSOAuthenticationUtils();