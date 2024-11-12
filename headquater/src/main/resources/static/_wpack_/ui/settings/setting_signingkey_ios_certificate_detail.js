/*amd /cm/ui/settings/setting_signingkey_ios_certificate_detail.xml 4657 a2d6df7ce0fabed084cd3ad9bc253131d1943e87a919aae8d3cbf459adbe7a99 */
define({declaration:{A:{version:'1.0',encoding:'UTF-8'}},E:[{T:1,N:'html',A:{xmlns:'http://www.w3.org/1999/xhtml','xmlns:ev':'http://www.w3.org/2001/xml-events','xmlns:w2':'http://www.inswave.com/websquare','xmlns:xf':'http://www.w3.org/2002/xforms'},E:[{T:1,N:'head',A:{},E:[{T:1,N:'w2:type',E:[{T:3,text:'COMPONENT'}]},{T:1,N:'w2:buildDate'},{T:1,N:'w2:MSA'},{T:1,N:'xf:model',E:[{T:1,N:'w2:dataCollection',A:{baseNode:'map'}},{T:1,N:'w2:workflowCollection'}]},{T:1,N:'w2:layoutInfo'},{T:1,N:'w2:publicInfo',A:{method:''}},{T:1,N:'script',A:{lazy:'false',type:'text/javascript'},E:[{T:4,cdata:function(scopeObj){with(scopeObj){
                        scwin.onpageload = function () {
                            const certificates = $p.getParameter("keyParam");

                            if (!!certificates) {
                                const certificatesJson = JSON.parse(certificates.ios_certificates_json);

                                if (Array.isArray(certificatesJson) && certificatesJson.length > 0) {
                                    ios_certificate_name_first.setValue(certificatesJson[0].certificate_key_name);
                                    ios_certificate_password_first.setValue(certificatesJson[0].certificate_password);

                                    let certificateName = common.util.findFileName(certificatesJson[0].certificate_path);
                                    before_ios_certificate_file_first.setValue(certificateName);

                                    if (certificatesJson.length > 1) {
                                        for (let certCnt = 1; certCnt < certificatesJson.length; certCnt++) {
                                            const genId = gen_ios_certificate.insertChild();

                                            gen_ios_certificate.getChild(genId, "os_certificate_name").setValue(certificatesJson[certCnt].certificate_key_name);
                                            gen_ios_certificate.getChild(genId, "ios_certificate_password").setValue(certificatesJson[certCnt].certificate_password);

                                            profileName = common.util.findFileName(certificatesJson[certCnt].certificate_path);
                                            gen_ios_certificate.getChild(genId, "before_ios_certificate_file").setValue(profileName);
                                        }
                                    }
                                }
                            }
                        };

                        scwin.addProfileOnclick = function () {
                            gen_ios_certificate.insertChild();
                        };

                        scwin.delProfileOnclick = function () {
                            gen_ios_certificate.removeChild(this.getGeneratedIndex());
                        };

                    }}}]}]},{T:1,N:'body',A:{'ev:onpageload':'scwin.onpageload'},E:[{T:1,N:'xf:group',A:{class:'tblbox',id:'',style:''},E:[{T:1,N:'xf:group',A:{adaptive:'layout',adaptiveThreshold:'800',class:'w2tb tbl',id:'',style:'',tagname:'table'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:summary'}]},{T:1,N:'xf:group',A:{tagname:'colgroup'},E:[{T:1,N:'xf:group',A:{style:'width:180px;',tagname:'col'}},{T:1,N:'xf:group',A:{style:'',tagname:'col'}}]},{T:1,N:'xf:group',A:{style:'',tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th req',style:'',tagname:'th'},E:[{T:1,N:'xf:group',A:{class:'tooltipbox',id:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_certificate_name'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',useLocale:'true',tooltip:'tooltip',tooltipDisplay:'true',tooltipLocaleRef:'lbl_certificate_name'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',style:'',tagname:'td'},E:[{T:1,N:'xf:input',A:{class:'',id:'ios_certificate_name_first',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'tooltip',useLocale:'true',localeRef:'lbl_signingkey_setting_ios_certificate_password'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_ios_certificate_pkcs_password',tooltip:'tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:secret',A:{class:'',id:'ios_certificate_password_first',style:'width:100%;'}}]}]},{T:1,N:'xf:group',A:{tagname:'tr'},E:[{T:1,N:'xf:group',A:{class:'w2tb_th ',tagname:'th'},E:[{T:1,N:'w2:attributes',E:[{T:1,N:'w2:scope',E:[{T:3,text:'row'}]}]},{T:1,N:'xf:group',A:{class:'tooltipbox',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{label:'',useLocale:'true',localeRef:'lbl_apple_dev_cert_file'}},{T:1,N:'w2:textbox',A:{class:'ico_tip',tagname:'span',useLocale:'true',tooltipDisplay:'true',tooltipLocaleRef:'lbl_signingkey_setting_ios_certificate_pkcs_file',tooltip:'tooltip'}}]}]},{T:1,N:'xf:group',A:{class:'w2tb_td',tagname:'td'},E:[{T:1,N:'xf:group',A:{class:'upload_grp'},E:[{T:1,N:'w2:attributes'},{T:1,N:'xf:group',A:{class:'flex',id:'',style:''},E:[{T:1,N:'w2:textbox',A:{id:'before_ios_certificate_file_first'}},{T:1,N:'input',A:{type:'file',id:'ios_certificate_file_first',style:'width:20%;',onchange:'common.util.inputFileChange(this)'}}]}]}]}]}]}]}]}]}]})