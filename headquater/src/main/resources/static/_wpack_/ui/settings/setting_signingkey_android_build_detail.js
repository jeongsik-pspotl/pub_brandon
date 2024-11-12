/*amd /cm/ui/settings/setting_signingkey_android_build_detail.xml 4569 ea7bb874705d42d9332f44cdc9debc3f2dad7964054725993e215bb9ecac5417 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
                        scwin.onpageload = function () {
                            const buildKeyParam = $p.getParameter("keyParam");

                            if (!!buildKeyParam) {
                                android_key_alias.setValue(buildKeyParam.android_key_alias);
                                android_alias_password.setValue(buildKeyParam.android_key_password);
                                android_key_store_password.setValue(buildKeyParam.android_key_store_password);

                                const filename = common.util.findFileName(buildKeyParam.android_key_path)
                                before_android_keystore_file.setValue(filename);
                            }
                        };

                    }}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{class:'',id:'',label:'Key Alias',useLocale:'true',localeRef:'lbl_signingkey_setting_android_key_alias'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_android_key_alias_tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:input',A:{class:'',id:'android_key_alias',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_signingkey_setting_android_key_password'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_android_key_password_tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:secret',A:{class:'',id:'android_alias_password',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_signingkey_setting_android_key_store_password'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_android_key_store_password_tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:secret',A:{class:'',id:'android_key_store_password',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'KeyStore File',useLocale:'true',localeRef:'lbl_signingkey_setting_android_key_store_file'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',tooltip:'tooltip',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_android_key_store_file_tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'upload_grp'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:colspan',E:[{T:3,text:'1'}]},{T:1,N:'w2:rowspan',E:[{T:3,text:'1'}]}]},{T:1,N:'w2:textbox',A:{id:'before_android_keystore_file',label:''}},{T:1,N:'input',A:{type:'file',id:'android_keystore_file',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}}]}]}]}]}]}]}]}]})