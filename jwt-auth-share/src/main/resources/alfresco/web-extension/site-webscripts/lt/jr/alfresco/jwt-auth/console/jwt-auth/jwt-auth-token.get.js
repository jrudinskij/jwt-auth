function buildPanel() {
    var model = {
        name: 'alfresco/core/ProcessWidgets',
        id: 'JWT_TOKEN_PANEL',
        config: {
            widgets : [
                {
                    name : 'alfresco/forms/Form',
                    config : {
                        widgets : [
                            {
                                name : 'alfresco/forms/controls/TextBox',
                                config : {
                                    style : 'vertical-align:top;',
                                    fieldId : 'user',
                                    name : 'user',
                                    label : 'jwt-auth-token.username',
                                    placeHolder : 'jwt-auth-token.username.placeHolder',
                                    requirementConfig : {
                                        initialValue : true
                                    },
                                    validationConfig: [
                                        {
                                            validation: "minLength",
                                            length: 2,
                                            errorMessage: "jwt-auth-token.username.required"
                                        }
                                    ],
                                    value: user.name
                                }
                            },
                            {
                                name : 'alfresco/forms/controls/TextBox',
                                config : {
                                    style : 'vertical-align:top;',
                                    fieldId : 'expireInDays',
                                    name : 'expireInDays',
                                    label : 'jwt-auth-token.expireInDays',
                                    placeHolder : 'jwt-auth-token.expireInDays.placeHolder',
                                    value: 1,
                                    requirementConfig : {
                                        initialValue : true
                                    },
                                    validationConfig: [
                                        {
                                          validation: "minLength",
                                          length: 1,
                                          errorMessage: "jwt-auth-token.expires.required"
                                        },
                                        {
                                            validation: "regex",
                                            regex: "^[0-9]*$",
                                            errorMessage: "jwt-auth-token.expires.numberOnly"
                                        }
                                    ]
                                }
                            }
                        ],
                        setValueTopic: 'JWT_AUTH_INIT_FORM',
                        okButtonLabel : 'jwt-auth-token.generate',
                        okButtonPublishTopic : 'JWT_AUTH_GET_TOKEN',
                        okButtonPublishGlobal : true,
                        showCancelButton : false
                    }
                },
                {
                    name : 'jwt-auth/html/JwtTokenDisplay'
                },

            ]
        }
    };
    return model;
}

model.jsonModel = {
    services : [ 'jwt-auth/service/JwtAuthService' ],
    widgets : [ {
        id : 'SET_PAGE_TITLE',
        name : 'alfresco/header/SetTitle',
        config : {
            title : 'tool.jwt-auth-token.label'
        }
    }, {
        name : 'alfresco/html/Label',
        config : {
            label : 'jwt-auth-token.intro-text',
            style : 'display: block; margin-bottom: 2ex;'
        }
    }, buildPanel() ]
};
