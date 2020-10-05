# ssoAuthentication 
- a light weight app containing files which are necessary to be loaded in the SSO popup window
- contains ssoAuthenticationInjector file which is responsible for communicating with SmartEdit app 

## SETUP
This light weight app follows a slightly different build process when compared with extensions based on SmartEdit. The setup includes
- All source files under `/src` directory
- Custom webpack configuration (`webpack.conf.js`)
- Custom typescript configuration (`tsconfig.json`)
- Two grunt tasks configured in `/smartedit-custom-build/config/shell.js` for both dev (`grunt shell:devbuildssoauthenticationinjector`) and prod (`grunt shell:prodbuildssoauthenticationinjector`) environments and added these tasks as part of `grunt dev` and `grunt packageSkipTests` respectively in `Gruntfile.js`