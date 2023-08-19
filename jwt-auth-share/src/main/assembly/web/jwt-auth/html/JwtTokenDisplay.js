define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/JwtTokenDisplay.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojo/dom-style",
        "alfresco/enums/urlTypes",
        "alfresco/util/urlUtils"],
    function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, lang, domConstruct, domStyle, urlTypes, urlUtils) {

        return declare([_WidgetBase, _TemplatedMixin, AlfCore], {

            cssRequirements: [{cssFile:"./css/JwtTokenDisplay.css"}],
            templateString: template,
            tokenLabel: "",
            token: "",
            expiresLabel: "",
            expires: "",

            /**
             * @instance
             */
            postMixInProperties: function JwtTokenDisplay__postMixInProperties() {
                this.tokenLabel = this.message('jwt-auth-token.token.label');
                this.expiresLabel = this.message('jwt-auth-token.expires.label');
                this.alfSubscribe('JWT_TOKEN_DISPLAY_SUCCESS', lang.hitch(this, this.onJwtTokenDisplayUpdate));
                this.alfSubscribe('JWT_TOKEN_DISPLAY_FAILURE', lang.hitch(this, this.onJwtTokenError))
            },

            onJwtTokenDisplayUpdate: function onJwtTokenDisplayUpdate(payload) {
                domStyle.set(this.tokenErrorNode, {
                    display: "none"
                });
                var response = lang.getObject('response', false, payload);
                if (response !== null && typeof response !== "undefined") {
                    this.token = response.token;
                    domConstruct.empty(this.tokenNode);
                    var tokenTextNode = document.createTextNode(response.token);
                    this.tokenNode.appendChild(tokenTextNode);

                    domConstruct.empty(this.expiresNode);
                    var tokenExpiresTextNode = document.createTextNode(response.expires);
                    this.expiresNode.appendChild(tokenExpiresTextNode);

                    domStyle.set(this.tokenDisplayNode, {
                        display: "block"
                    });
                } else {
                    domStyle.set(this.tokenDisplayNode, {
                        display: "none"
                    });
                }
            },

            __onClick: function copyToClipboard() {
                navigator.clipboard.writeText(this.token);
            },

            onJwtTokenError: function onJwtTokenError(payload) {
                var response = lang.getObject('response', false, payload);

                domStyle.set(this.tokenDisplayNode, {
                    display: "none"
                });

                domStyle.set(this.tokenErrorNode, {
                    display: "block"
                });

                domConstruct.empty(this.tokenErrorNode);
                var message = JSON.parse(response.response.text).message;
                var tokenErrorTextNode = document.createTextNode(message);
                this.tokenErrorNode.appendChild(tokenErrorTextNode);
            }
        });
    });