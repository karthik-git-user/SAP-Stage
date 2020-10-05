import {ssoAuthenticationUtils} from 'smarteditcepcontainer/services/authentication/SSOAuthenticationUtils';

if (ssoAuthenticationUtils.isSSODialog()) {
	ssoAuthenticationUtils.completeDialog();
}