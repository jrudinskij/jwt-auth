define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/core/CoreXhr",
        "service/constants/Default"],
    function(declare, Core, lang, CoreXhr, AlfConstants) {

        return declare([Core, CoreXhr], {

            constructor: function jwtauth_service_JwtAuthService__constructor(args) {
                lang.mixin(this, args);
                this.alfSubscribe("JWT_AUTH_GET_TOKEN", lang.hitch(this, this.getToken));
            },

            getToken: function jwtauth_service_JwtAuthService__getToken(payload) {
                this.serviceXhr({
                    url: AlfConstants.PROXY_URI + 'jr/authentication/jwt/' + payload.user + '/' + payload.expireInDays,
                    method: "GET",
                    alfTopic: 'JWT_TOKEN_DISPLAY'
                });
            }
        });
    });