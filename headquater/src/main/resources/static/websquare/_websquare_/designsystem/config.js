export default {
    "WebSquare": {
        "convertPageXML": {
            "@value": "true"
        },
        "wpack": {
            "@use": "true",
            "contextRoot": {
                "@value": "/"
            },
            "srcExtension": {
                "@value": "xml"
            },
            "destExtension": {
                "@value": "js"
            },
            "destRoot": {
                "@value": "_wpack_"
            }
        },
        "allValue": {
            "@value": "all"
        },
        "docType": {
            "@value": "standard"
        },
        "debug": {
            "@value": "true",
            "@remoteConsole": "false",
            "@errorConsole": "true"
        },
        "debugKey": {
            "@value": ""
        },
        "language": {
            "@value": "ko"
        },
        "date": {
            "requestInterval": {
                "@value": "10000"
            }
        },
        "exceptionHandler": {
            "@value": "websquare"
        },
        "debugMenu": {
            "@value": "use"
        },
        "welcome-file": {},
        "postDrawMode": {
            "@value": "synchronous"
        },
        "forcedValueSetting": {
            "@value": "true"
        },
        "processMsgHeight": {
            "@value": "81"
        },
        "processMsgWidth": {
            "@value": "295"
        },
        "processMsgBackground": {
            "@value": "true",
            "@backgroundColor": "#112233"
        },
        "byteCheckEncoding": {
            "@value": "euc-kr"
        },
        "initScript": {
            "@value": "false"
        },
        "clearMemory": {
            "@value": "false"
        },
        "stylesheet": {
            "@value": "stylesheet_ext.css",
            "@import": "link",
            "@enable": "true",
            "@earlyImportList": ""
        },
        "style": {
            "removeDefaultClass": {
                "@value": "true"
            }
        },
        "engine": {},
        "ModelUtil": {
            "copyChildrenNodes": {
                "@refresh": "false"
            }
        },
        "preProcessor": {
            "systemPreProcessor": {
                "@value": ""
            },
            "businessPreProcessor": {
                "@value": ""
            }
        },
        "workflow": {
            "processMsg": {
                "@value": ""
            },
            "makeGlobalObject": {
                "@value": "true"
            }
        },
        "submission": {
            "processMsg": {
                "@value": ""
            },
            "showSubmissionTime": {
                "@value": "true"
            },
            "preSubmitFunction": {
                "@value": ""
            },
            "callbackSubmitFunction": {
                "@value": ""
            },
            "requestID": {
                "@value": ""
            },
            "makeGlobalObject": {
                "@value": "true"
            }
        },
        "visibleHelper": {
            "targetHandler": {
                "@value": ""
            }
        },
        "spa": {
            "onpageunload": {
                "@value": ""
            },
            "variable": {
                "@value": "scwin"
            },
            "scriptCache": {
                "@value": "true"
            },
            "autoReload": {
                "@value": "true",
                "@count": "50"
            }
        },
        "scriptLoading": {
            "@merge": "true"
        },
        "scriptPrecedence": {
            "@value": "true"
        },
        "strictMode": {
            "@value": "true",
            "@id": "mf"
        },
        "engineCache": {
            "@compression": "true",
            "@enable": "false",
            "@postfix": "month"
        },
        "userAgentPattern": {
            "@XPathParser": "Edge"
        },
        "preserveWhiteSpace": {
            "@value": "false"
        },
        "environment": {
            "@mode": "production",
            "@cache": "nocache",
            "@postfix": "day"
        },
        "script": {
            "@postfix": "environment"
        },
        "emptyTag": {
            "@value": "area,base,basefont,br,col,frame,hr,img,input,link,meta,param"
        },
        "engineLoadingMode": {
            "@ie": "0",
            "@moz": "0",
            "@opera": "0",
            "@android": "0",
            "@iphone": "0",
            "@chrome": "0",
            "@safari": "0"
        },
        "dataList": {
            "removeDummyRowStatus": {
                "@value": "true"
            },
            "removedDataMatch": {
                "@value": "true"
            }
        },
        "grid": {
            "rowNumStatusUniqueId": {
                "@value": "true"
            },
            "drilldownRealRowIndexAll": {
                "@value": "true"
            },
            "collectGarbage": {
                "@value": "none"
            },
            "fastScroll": {
                "@value": "true"
            },
            "dataType": {
                "date": {
                    "@displayFormat": "yyyy-MM-dd"
                }
            },
            "editType": {
                "@value": "focus"
            },
            "rowNumBackgroundColor": {
                "@value": "#e5eff7"
            },
            "selectedRowColor": {
                "@value": "#fcf8e3"
            },
            "oddEvenColorDisplay": {
                "@value": "true"
            },
            "evenRowBackgroundColor": {
                "@value": "#f5f5f5"
            },
            "oddRowBackgroundColor": {
                "@value": "#ffffff"
            },
            "rowMouseOver": {
                "@value": "true"
            },
            "rowMouseOverColor": {
                "@value": "#edf3fb"
            },
            "tooltipStyle": {
                "@value": "padding:1px 3px 0;line-height:14px;font-size:12px;border:0;background-color:#5c85d4;color:#fff"
            },
            "noResultMessageVisible": {
                "@value": "true"
            },
            "noResultMessage": {
                "@value": "데이터가 없음"
            },
            "noResultMessageStyle": {
                "@value": "position:absolute; left:40%; width:20%; top:40%; border:1px solid #b3b3b3; color:#383d41; font-size:12px; padding:5px; text-align:center; background:#fafafa"
            },
            "pasteMessage": {
                "@value": "데이터 붙이는 중"
            },
            "getColumnVisible": {
                "@useRealColIndex": "true"
            },
            "colIdToColIndex": {
                "@value": "true"
            },
            "column": [{
                    "@inputType": "text",
                    "editType": {
                        "@value": "select"
                    }
                },
                {
                    "@inputType": "select",
                    "chooseOptionLabel": {
                        "@value": "-선택-"
                    }
                },
                {
                    "@inputType": "calendar",
                    "dataType": {
                        "@value": "date"
                    },
                    "displayFormat": [{
                            "@valueType": "yearMonth",
                            "@value": "yyyy-MM"
                        },
                        {
                            "@valueType": "yearMonthDate",
                            "@value": "yyyy-MM-dd"
                        },
                        {
                            "@valueType": "yearMonthDateTime",
                            "@value": "yyyy-MM-dd HH:mm"
                        },
                        {
                            "@valueType": "yearMonthDateTimeSec",
                            "@value": "yyyy-MM-dd HH:mm:SS"
                        }
                    ]
                }
            ]
        },
        "gridView": {
            "drilldownFooterExpressionAllData": {
                "@value": "true"
            },
            "rowNumStatusUniqueId": {
                "@value": "true"
            },
            "preventMultipleClick": {
                "@value": "true"
            },
            "drilldownRealRowIndexAll": {
                "@value": "true"
            },
            "collectGarbage": {
                "@value": "none"
            },
            "fastScroll": {
                "@value": "true"
            },
            "dataType": {
                "date": {
                    "@displayFormat": "yyyy-MM-dd"
                }
            },
            "editType": {
                "@value": "focus"
            },
            "rowNumBackgroundColor": {
                "@value": "#e5eff7"
            },
            "selectedRowColor": {
                "@value": "#fcf8e3"
            },
            "oddEvenColorDisplay": {
                "@value": "true"
            },
            "evenRowBackgroundColor": {
                "@value": "#f5f5f5"
            },
            "oddRowBackgroundColor": {
                "@value": "#ffffff"
            },
            "rowMouseOver": {
                "@value": "true"
            },
            "rowMouseOverColor": {
                "@value": "#edf3fb"
            },
            "tooltipStyle": {
                "@value": "padding:1px 3px 0;line-height:14px;font-size:12px;border:0;background-color:#5c85d4;color:#fff"
            },
            "noResultMessageVisible": {
                "@value": "true"
            },
            "noResultMessage": {
                "@value": "데이터가 없음"
            },
            "noResultMessageStyle": {
                "@value": "position:absolute; left:40%; width:20%; top:40%; border:1px solid #b3b3b3; color:#383d41; font-size:12px; padding:5px; text-align:center; background:#fafafa"
            },
            "pasteMessage": {
                "@value": "데이터 붙이는 중"
            },
            "getColumnVisible": {
                "@useRealColIndex": "true"
            },
            "colIdToColIndex": {
                "@value": "true"
            },
            "column": [{
                    "@inputType": "text",
                    "editType": {
                        "@value": "select"
                    }
                },
                {
                    "@inputType": "select",
                    "chooseOptionLabel": {
                        "@value": "-선택-"
                    },
                    "eventPriority": {
                        "@value": "oneditend"
                    }
                },
                {
                    "@inputType": "calendar",
                    "dataType": {
                        "@value": "date"
                    },
                    "displayFormat": [{
                            "@valueType": "yearMonth",
                            "@value": "yyyy-MM"
                        },
                        {
                            "@valueType": "yearMonthDate",
                            "@value": "yyyy-MM-dd"
                        },
                        {
                            "@valueType": "yearMonthDateTime",
                            "@value": "yyyy-MM-dd HH:mm"
                        },
                        {
                            "@valueType": "yearMonthDateTimeSec",
                            "@value": "yyyy-MM-dd HH:mm:SS"
                        }
                    ]
                }
            ]
        },
        "inputCalendar": {
            "validCheck": {
                "@value": "false"
            },
            "dataType": {
                "@value": "date"
            },
            "delimiter": {
                "@value": "-"
            },
            "mask": {
                "@value": "MM-dd-yyyy"
            },
            "delimiterLocaleKey": {
                "@value": "IC_Delimiter"
            },
            "maskLocaleKey": {
                "@value": "IC_Mask"
            },
            "calendarImage": {
                "@value": ""
            },
            "calendarImageOver": {
                "@value": ""
            }
        },
        "input": {
            "focusStyle": {
                "@value": ""
            },
            "dateMask": {
                "@value": "yyyy-MM-dd"
            },
            "timeMask": {
                "@value": "HH:mm"
            },
            "focusCalcSize": {
                "@value": "false"
            }
        },
        "calendar": {
            "minYear": {
                "@value": "1978"
            },
            "maxYear": {
                "@value": "2030"
            }
        },
        "selectbox": {
            "visibleRowNum": {
                "@value": "5"
            },
            "dataListAutoRefresh": {
                "@value": "true"
            }
        },
        "tabControl": {},
        "treeview": {
            "tooltipGroupClass": {
                "@value": "false"
            }
        },
        "trigger": {
            "preventMultipleClick": {
                "@value": "true"
            }
        },
        "anchor": {
            "preventMultipleClick": {
                "@value": "true"
            }
        },
        "wframe": {
            "mode": {
                "@value": "async"
            },
            "scope": {
                "@value": "true"
            }
        },
        "pageInherit": {
            "mode": {
                "@value": "sync"
            }
        },
        "windowContainer": {
            "tooltipGroupClass": {
                "@value": "false"
            },
            "getWindow": {
                "@searchTarget": "windowId"
            },
            "displayOnlyTopWindow": {
                "@value": "true"
            }
        },
        "pageList": {},
        "radio": {
            "nameScope": {
                "@value": "true"
            }
        },
        "body": {
            "toolTipSec": {
                "@value": "1"
            }
        },
        "editor": {
            "version": {
                "@value": "4.11.3"
            }
        },
        "fusionchart": {},
        "languagePack": {
            "@useLanguagePack": "false",
            "url": [{
                    "@lang": "ko",
                    "@value": "/langpack/ko.js"
                },
                {
                    "@lang": "en",
                    "@value": "/langpack/en.js"
                },
                {
                    "@lang": "ch",
                    "@value": "/langpack/ch.js"
                }
            ]
        },
        "license": {
            "@value": "false"
        }
    }
}