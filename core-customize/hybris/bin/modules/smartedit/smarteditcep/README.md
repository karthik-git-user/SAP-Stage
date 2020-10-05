# smarteditcep
SmartEdit CEP (Cloud Extension Pack) Extension

This extension is created on top of ysmarteditmodule and it requires samlsinglesignon extension. It provides SSO functionality. Smartedit 1905 already provides login functionality using credentials approach. In order to include SSO functionality for SmartEdit as part of SAP Commerce 1905 version, this extension is required on top of the latest SAP Commerce 1905 PATCH release. SmartEdit achieves SSO by overwriting few files in this extension from SmartEdit such as `AuthenticationServiceOuter.ts`, `LoginModalService.ts`, `loginDialog.html` renamed to `ssoLoginDialog.html` and corresponsing css file `loginDialog.scss`. 

SSO in SmartEdit can be enabled using below projecties set in `project.properties` file
- `smartedit.sso.enabled=true`
- `smartedit.bootstrapOuterExtensions.smarteditcep=smarteditcepContainer:/smarteditcep/smarteditcep/js/smarteditcepContainer.js`