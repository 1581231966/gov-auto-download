var historyModule = angular.module("history", []);
var loginPage = '/pc/';
var STATES = ['AK', 'AL', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DC', 'DE', 'FL', 'GA', 'HI', 'IA', 'ID', 'IL', 'IN', 'KS', 'KY', 'LA', 'MA', 'MD', 'ME', 'MI', 'MN', 'MO', 'MS', 'MT', 'NC', 'ND', 'NE', 'NH', 'NJ', 'NM', 'NV', 'NY', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VA', 'VT', 'WA', 'WI', 'WV', 'WY', 'PR', 'VI'];
var canOperateMCPC = 'ROLE_ADMIN, ROLE_ACCOUNT_SPECIALIST, ROLE_CARRIER, ROLE_PRINCIPAL_CARRIER';
var jiraLocation = 'https://jira.ehealthinsurance.com/browse/';

historyModule.controller('historyCtrl', [
		'$scope',
		'$http',
		'$window',
		'FetchDataService',
		function($scope, $http, $window, FetchDataService) {
			
			$scope.noResultFind = 'There is no result found with your condition !';

			$scope.pageSize = 20;
			$scope.sizeList = [20, 50, 100, 1000]
			$scope.totalPage = 1;
			$scope.currentPage = 1;
			$scope.totalNum = 0;
			$scope.pages = [];
			$scope.totalPageMDP = 1;
			$scope.currentPageMDP = 1;
			$scope.totalNumMDP = 0;
			$scope.pagesMDP = [];
			$scope.totalPagePN = 1;
			$scope.currentPagePN = 1;
			$scope.totalNumPN = 0;
			$scope.pagesPN = [];
			$scope.totalPageCDL = 1;
			$scope.currentPageCDL = 1;
			$scope.totalNumCDL = 0;
			$scope.pagesCDL = [];
            $scope.totalPageFM = 1;
            $scope.currentPageFM = 1;
            $scope.totalNumFM = 0;
            $scope.pagesFM = [];
            $scope.totalPageSPB = 1;
            $scope.currentPageSPB = 1;
            $scope.totalNumSPB = 0;
            $scope.pagesSPB= [];
			$scope.states = STATES;
			$scope.orderPro = 'updatedWhen';
			$scope.stateVal= "select state(s)";
			$scope.selectStates = [];
			$scope.pageType = "HPMS";
			$scope.historyHeader = "HPMS Loading Task Search Result:";
			$scope.selectPlans = [];
			$scope.selectPlan = '';
            $scope.availableMySqlPlanCount = 0;
            $scope.availableOraclePlanCount = 0;

			function initTotalNum() {
				var params = $('#filterForm').serialize().replace('select+state(s)','') + '&pageType=' + $scope.pageType;
				FetchDataService.total(params).success(function(data) {
					if ($scope.pageType == 'MDP') {
						$scope.totalNumMDP = data;
					} 
					if ($scope.pageType == 'planNetwork') {
						$scope.totalNumPN = data;
					}
					if($scope.pageType == 'HPMS'){
					    $scope.totalNum = data;
					}
					if($scope.pageType == 'CDL'){
					    $scope.totalNumCDL = data;
					}
                    if($scope.pageType == 'FM'){
                        $scope.totalNumFM = data;
                    }
                    if($scope.pageType == 'SPB'){
                        $scope.totalNumSPB = data;
                    }
					initPages();
				});
			}
			
			function initPages() {
				var totalPage;
				var currentPage;
				if($scope.pageType == 'HPMS'){
					totalPage = Math.ceil($scope.totalNum / $scope.pageSize);
					currentPage = $scope.currentPage;
				} 
				if($scope.pageType == 'planNetwork'){
					totalPage = Math.ceil($scope.totalNumPN / $scope.pageSize);
					currentPage = $scope.currentPagePN;
				} 
				if($scope.pageType == 'MDP'){
					totalPage = Math.ceil($scope.totalNumMDP / $scope.pageSize);
					currentPage = $scope.currentPageMDP;
				}
				if($scope.pageType == 'CDL'){
					totalPage = Math.ceil($scope.totalNumCDL / $scope.pageSize);
					currentPage = $scope.currentPageCDL;
				}
                if($scope.pageType == 'FM'){
                    totalPage = Math.ceil($scope.totalNumFM / $scope.pageSize);
                    currentPage = $scope.currentPageFM;
                }
                if($scope.pageType == 'SPB'){
                    totalPage = Math.ceil($scope.totalNumSPB / $scope.pageSize);
                    currentPage = $scope.currentPageSPB;
                }
				if (typeof(currentPage) == "undefined" || currentPage == '') {
					currentPage = 1;
				}
				var pages;
				if (currentPage > 1 && currentPage < totalPage) {
					pages = [parseInt(currentPage) - 1, currentPage, parseInt(currentPage) + 1 ];
				} else if (currentPage == 1 && totalPage > 1) {
					pages = [currentPage, parseInt(currentPage) + 1 ];
				} else if (currentPage == totalPage && totalPage > 1) {
					pages = [parseInt(currentPage) - 1, currentPage ];
				}
				if($scope.pageType == 'HPMS'){
					$scope.totalPage = totalPage;
					$scope.pages = pages;
				} 
				if($scope.pageType == 'planNetwork'){
					$scope.totalPagePN = totalPage;
					$scope.pagesPN = pages;
				} 
				if($scope.pageType == 'MDP'){
					$scope.totalPageMDP = totalPage;
				    $scope.pagesMDP = pages;
				}
				if($scope.pageType == 'CDL'){
					$scope.totalPageCDL = totalPage;
				    $scope.pagesCDL = pages;
				}
                if($scope.pageType == 'FM'){
                    $scope.totalPageFM = totalPage; 
                    $scope.pagesFM = pages;
                }
                if($scope.pageType == 'SPB'){
                    $scope.totalPageSPB = totalPage;
                    $scope.pagesSPB = pages;
                }
			}
			
			$scope.getState = function(obj){
				var id = '#' + obj.state;
				var isChecked = $(id).is(':checked');
				if(isChecked){
					$scope.selectStates.push(obj.state);
				} else {
					$scope.selectStates.splice(jQuery.inArray(obj.state,$scope.selectStates),1); ;
				}
				if($scope.selectStates.length==0){
					$scope.stateVal = "select state(s)";
				}else{
					$scope.stateVal = $scope.selectStates.join(",");
				}
				$("#isBlankState").attr("checked", false);
			}
			
			$scope.getBlankState = function(obj){
				var id = '#isBlankState';
				var isChecked = $(id).is(':checked');
				if(isChecked){
					$scope.resetState();
					$scope.selectStates = ['blank'];
					$scope.stateVal = 'blank';
					$("#stateValue").css('display','none'); 
					$("#textValue").html("select state(s)");
				} else {
					$scope.selectStates = [];
					$scope.stateVal = 'select state(s)';
					$("#stateValue").css('display',''); 
					$("#textValue").html("");
				}
			}
			
			function _loadName() {
				 $http.get("/mcpc/api/hpms/getName").success(function(data){
					 if(data=="unlogin"){
						  window.location.href = loginPage;
						  return;
					 }
					 var obj = data[0];
					 $scope.userName = obj.UserName;
					 $scope.Role = obj.Role;
					 $scope.loadBy = obj.UserName;
					 
			    	 if(canOperateMCPC.indexOf($scope.Role)==-1){
			    		$scope.hasPermissionOperate = true;
			    		return;
			    	 }
			    	 $scope.hasPermissionOperate = false;
				});	
			}
			
			function _initPageType(){
				var type = FetchDataService.getPageType();
				if(type=='' || angular.isUndefined(type)){
					return;
				}
				$scope.pageType = type;
				
				if(type=="MDP"){
					$scope.historyHeader = "Document Loading Search Result:";
				}
				
				if(type=="HPMS"){
					$scope.historyHeader = "HPMS Loading Task Search Result:";
				}
				
				if(type=="planNetwork"){
					$scope.historyHeader = "Plan Network Task Search Result:";
				}
				if(type=="CDL"){
					$scope.historyHeader = "CMS Data Loading Search Result:";
				}
                if(type=="FM"){
                    $scope.historyHeader = "Formulary Loading Search Result:";
                }
                if(type=="SPB"){
                    $scope.historyHeader = "Structured Plan Benefit Search Result:";
                }
			}
			
			$scope.setOrderProp = function(obj){
				if($scope.orderPro == obj){
					$scope.orderPro = '-' + obj;
					return;
				 }
			       $scope.orderPro = obj;
			}

			$scope.load = function() {
				var currentPage;
				if ($scope.pageType == 'HPMS'){
					currentPage = $scope.currentPage;
				} 
				if ($scope.pageType == 'planNetwork'){
					currentPage = $scope.currentPagePN;
				} 
				if ($scope.pageType == 'MDP'){
					currentPage = $scope.currentPageMDP;
				}
				if ($scope.pageType == 'CDL'){
					currentPage = $scope.currentPageCDL;
				}
                if ($scope.pageType == 'FM'){
                    currentPage = $scope.currentPageFM;
                }
                if ($scope.pageType == 'SPB'){
                    currentPage = $scope.currentPageSPB;
                }
				var params = $('#filterForm').serialize().replace('select+state(s)','') + '&currentPage='
						+ currentPage + '&pageSize=' + $scope.pageSize + '&pageType='+$scope.pageType;
				if(FetchDataService.validateDate()){
					return;
				}
				FetchDataService.block();
				FetchDataService.post(params).success(function(data){
					if(data == "unlogin"){
						window.location.href = loginPage;
						return;
					}
					
					if(data == '' || angular.isUndefined(data)){
						if($scope.pageType == 'HPMS'){
							$scope.isEmptyResult = true;
						} 
						if($scope.pageType == 'planNetwork'){
							$scope.isEmptyResultPN = true;
						} 
						if($scope.pageType == 'MDP'){
							$scope.isEmptyResultMDP = true;
						}
						if($scope.pageType == 'CDL'){
							$scope.isEmptyResultCDL = true;
						}
                        if($scope.pageType == 'FM'){
                            $scope.isEmptyResultFM = true;
                        }
                        if($scope.pageType == 'SPB'){
                            $scope.isEmptyResultSPB = true;
                        }
					}else{
						if($scope.pageType == 'HPMS'){
							$scope.isEmptyResult = false;
						} 
						if($scope.pageType == 'planNetwork'){
							$scope.isEmptyResultPN = false;
						} 
						if($scope.pageType == 'MDP'){
							$scope.isEmptyResultMDP = false;
						}
						if($scope.pageType == 'CDL'){
							$scope.isEmptyResultCDL = false;
						}
                        if($scope.pageType == 'FM'){
                            $scope.isEmptyResultFM = false;
                        }
                        if($scope.pageType == 'SPB'){
                            $scope.isEmptyResultSPB = false;
                        }
					}
					initPages();
					if($scope.pageType == 'HPMS'){
						$scope.plans = data;
					} 
					if($scope.pageType == 'planNetwork'){
						$scope.planNetworks = data;
					} 
					if($scope.pageType == 'MDP'){
						$scope.documentPlans = data;
					}
					if($scope.pageType == 'CDL'){
						$scope.planCrossWalks = data;
					}
                    if($scope.pageType == 'FM'){
                        $scope.formularies = data;
                    }
                    if($scope.pageType == 'SPB'){
                    	angular.forEach(data, function(ele, index) {
                    		ele.benefitsStr = JSON.stringify(ele.benefits);                    		
                    		ele.basicInfo = {};
                    		ele.basicInfo.id = ele.id;
                    		ele.basicInfo.cmsContractId = ele.cmsContractId;
                    		ele.basicInfo.cmsPlanId = ele.cmsPlanId;
                    		ele.basicInfo.cmsSegmentId = ele.cmsSegmentId;
                    		ele.basicInfo.cmsPlanYear = ele.cmsPlanYear;
                    		ele.basicInfo.cmsJiraNumber = ele.cmsJiraNumber;
                    	});
                        $scope.structuredPlanBenefits = data;
                        $scope.orderPro = "cmsContractId";
                    }
                    $scope.selectAllPlan = false;
					$scope.selectPlans = [];
					$scope.selectPlan = '';
					closeBg();
				}).error(function(){
					closeBg();
			    });
			};
			$scope.changeSize = function() {
				$scope.pageSize = $scope.testSize;
				if($scope.pageType == 'HPMS'){
					$scope.pageSize = $scope.newSize;
				}
				if($scope.pageType == 'planNetwork'){
					$scope.pageSize = $scope.newSizePN;
				}
				if($scope.pageType == 'MDP'){
					$scope.pageSize = $scope.newSizeMDP;
				}
				if($scope.pageType == 'CDL'){
					$scope.pageSize = $scope.newSizeCDL;
				}
				if($scope.pageType == 'FM'){
					$scope.pageSize = $scope.newSizeFM;
				}
				if($scope.pageSize == 'SPB'){
				$scope.pageSize = $scope.newSizeSPB;
				}
                $scope.load();
			};
			
			$scope.resetState = function(){
				$scope.stateVal= "select state(s)";
				$(".checkBoxSelect").each(function() {
					$(this).attr("checked", false);
				});
				$scope.selectStates = [];
				$("#stateCheckBox").hide();
			};

			$scope.next = function() {
				if ($scope.pageType == 'HPMS'){
					if ($scope.currentPage < $scope.totalPage) {
						$scope.currentPage++;
					}
				} 
				if ($scope.pageType == 'planNetwork'){
					if ($scope.currentPagePN < $scope.totalPage) {
						$scope.currentPagePN++;
					}
				} 
				if ($scope.pageType == 'MDP'){
					if ($scope.currentPageMDP < $scope.totalPageMDP) {
						$scope.currentPageMDP++;
					}
				}
				if ($scope.pageType == 'CDL'){
					if ($scope.currentPageCDL < $scope.totalPageCDL) {
						$scope.currentPageCDL++;
					}
				}
                if ($scope.pageType == 'FM'){
                    if ($scope.currentPageFM < $scope.totalPageFM) {
                        $scope.currentPageFM++;
                    }
                }
                if ($scope.pageType == 'SPB'){
                    if ($scope.currentPageSPB < $scope.totalPageSPB) {
                        $scope.currentPageSPB++;
                    }
                }
				$scope.load();
			};

			$scope.prev = function() {
				if ($scope.pageType == 'HPMS'){
					if ($scope.currentPage > 1) {
						$scope.currentPage--;
					}
				} 
				if ($scope.pageType == 'planNetwork'){
					if ($scope.currentPagePN > 1) {
						$scope.currentPagePN--;
					}
				} 
				if ($scope.pageType == 'MDP'){
					if ($scope.currentPageMDP > 1) {
						$scope.currentPageMDP--;
					}
				}
				if ($scope.pageType == 'CDL'){
					if ($scope.currentPageCDL > 1) {
						$scope.currentPageCDL--;
					}
				}
                if ($scope.pageType == 'FM'){
                    if ($scope.currentPageFM > 1) {
                        $scope.currentPageFM--;
                    }
                }
                if ($scope.pageType == 'SPB'){
                    if ($scope.currentPageSPB > 1) {
                        $scope.currentPageSPB--;
                    }
                }
				$scope.load();
			};

			$scope.loadPage = function(page) {
				if ($scope.pageType == 'HPMS'){
					$scope.currentPage = page;
				} 
				if ($scope.pageType == 'planNetwork'){
					$scope.currentPagePN = page;
				} 
				if ($scope.pageType == 'MDP'){
					$scope.currentPageMDP = page;
				}
				if ($scope.pageType == 'CDL'){
					$scope.currentPageCDL = page;
				}
                if ($scope.pageType == 'FM'){
                    $scope.currentPageFM = page;
                }
                if ($scope.pageType == 'SPB'){
                    $scope.currentPageSPB = page;
                }
				$scope.load();
			};

			$scope.init = function() {
				if ($scope.pageType == 'MDP') {
					$scope.currentPageMDP = 1;
				} 
				if ($scope.pageType == 'planNetwork') {
					$scope.currentPagePN = 1;
				} 
				if ($scope.pageType == 'HPMS'){
				    $scope.currentPage = 1;
				}
				if ($scope.pageType == 'CDL'){
				    $scope.currentPageCDL = 1;
				}
                if ($scope.pageType == 'FM'){
                    $scope.currentPageFM = 1;
                }
                if ($scope.pageType == 'SPB'){
                    $scope.currentPageSPB = 1;
                }
				initTotalNum();
				$scope.load();
			}
			
			$scope.initByKeyEnter = function(event) {
				if (event.keyCode == 13) {
					$scope.init();
				}
			}
			
			$scope.buildJiraLink = function(str){
				return FetchDataService.buildJiraLink(str);
			}
			
			$scope.showUpdateJiraBox = function(id, oldJirNum, updateType){			
			    layer.prompt({
			    	value: oldJirNum,
			        title: 'You can input your jira number !'
			    },function(value, index, elem){
			    	var reg = new RegExp('[A-Za-z]{4}-[0-9]+');
			    	if(reg.test(value)){
				    	$http.get('/mcpc/api/hpms/updateJiraNum?jiraNum=' + value + '&id=' + id + '&updateType=' + updateType)
				    	.success(function(data){
							$scope.load();
							layer.close(index);
						})
			    	} else {
			    		alert('Please input the right format jira number, eg: PCPM-1234');
			    	}

			    });			
			}
			
			//MDP = Medicare Document Pdf
			$scope.changePageType = function(type){
				$scope.pageType = type;
				if(type=="MDP"){
					$scope.historyHeader = "Document Loading Search Result:";
				}
				
				if(type=="HPMS"){
					$scope.historyHeader = "HPMS Loading Task Search Result:";
				}
				
				if(type=="planNetwork"){
					$scope.historyHeader = "Plan Network Task Search Result:";
				}
				
				if(type=="CDL"){
					$scope.historyHeader = "CMS Data Loading Search Result:";
				}
                
                if(type=="FM"){
                    $scope.historyHeader = "Formulary Loading Search Result:";
                }
                if(type=="SPB"){
					$scope.historyHeader = "Structured Plan Benefit Search Result:";
				}
			}
			
			$scope.downloadPDF = function(fileId){
				window.location = "/mcpc/api/hpms/downloadPDFDocument?fileId="+ fileId;
			}
			
			$scope.goHome = function(){
				window.open('upload-hpms-file.html?pageType='+$scope.pageType);
			}
            
            $scope.goCustomization = function(){
                window.open('data-customization.html?pageType=PDTC');
            }
			
			$scope.getPDFNameClass = function(status) {			
				if (status == 'SubmitSuccess') {
					return 'pdf-submit-success';
				}
				if (status == 'DownloadFail') {
					return 'pdf-download-fail';
				}
				return 'pdf-downloaded';
			}
			
			$scope.getPDFTitle = function(jiraNum, downloadMsg) {
				if (!downloadMsg) {
					return jiraNum;
				}
				return downloadMsg;
			}
			
			$scope.collectJiraNums = function(plan){
				var array = new Array();
				var sobJiraNum = plan.summaryOfBenefitJiraNum;
				var srJiraNum = plan.startRatingsJiraNum;
				//var mldJiraNum = plan.multiLanguageDocumentJiraNum;
				var eocJiraNum = plan.evidenceOfCoverageJiraNum;
				var ndnJiraNum = plan.nonDiscriminationNoticeJiraNum;
				
				var sobTaskId = plan.summaryOfBenefitTaskId;
				var srTaskId = plan.startRatingsTaskId;
				//var mldTaskId = plan.multiLanguageDocumentTaskId;
				var eocTaskId = plan.evidenceOfCoverageTaskId;
				var ndnTaskId = plan.nonDiscriminationNoticeTaskId;
				
				if(sobJiraNum != ""){
					array[0] = {jiraNum:sobJiraNum, taskId:sobTaskId};
				}
				if(srJiraNum != "" && srJiraNum != sobJiraNum){
					array.push({jiraNum:srJiraNum, taskId:srTaskId});
				} else if (srJiraNum != "" && srJiraNum == sobJiraNum && srTaskId != sobTaskId){
					array.push({jiraNum:srJiraNum + '_SR',taskId:srTaskId});
				}
				/*if(mldJiraNum != "" && mldJiraNum != sobJiraNum && mldJiraNum != srJiraNum){
					array.push({jiraNum:mldJiraNum, taskId:mldTaskId});
				} else if (mldJiraNum != "" && mldTaskId != sobTaskId && mldTaskId != srTaskId && (mldJiraNum == sobJiraNum || mldJiraNum == srJiraNum)) {
					array.push({jiraNum:mldJiraNum + '_MT',taskId:mldTaskId});
				}*/
				if(eocJiraNum != "" && eocJiraNum != sobJiraNum && eocJiraNum != srJiraNum /*&& eocJiraNum != mldJiraNum*/){
					array.push({jiraNum:eocJiraNum, taskId:eocTaskId});
				} else if (eocJiraNum != "" && eocTaskId != sobTaskId && eocTaskId != srTaskId /*&& eocTaskId != mldTaskId && (eocJiraNum == sobJiraNum || eocJiraNum == srJiraNum /*|| eocJiraNum == mldJiraNum)*/) {
					array.push({jiraNum:eocJiraNum + '_EOC',taskId:eocTaskId});
				}
				if(ndnJiraNum != "" && ndnJiraNum != sobJiraNum && ndnJiraNum != srJiraNum /*&& ndnJiraNum != mldJiraNum*/ && ndnJiraNum != eocJiraNum){
					array.push({jiraNum:ndnJiraNum, taskId:ndnTaskId});
				} else if (ndnJiraNum != "" && ndnTaskId != sobTaskId && ndnTaskId != srTaskId /*&& ndnTaskId != mldTaskId*/ && ndnTaskId != eocJiraNum && (ndnJiraNum == sobJiraNum || ndnJiraNum == srJiraNum /*|| ndnJiraNum == mldJiraNum*/ || ndnJiraNum == eocJiraNum)){
					array.push({jiraNum:ndnJiraNum + '_NDN',taskId:ndnTaskId});
				}
				$scope.jiraNumbers = array;
				
				var taskId;
				if (sobTaskId) {
					taskId = sobTaskId;
				} else if (srTaskId) {
					taskId = srTaskId;
				} /*else if (mldTaskId) {
					taskId = mldTaskId;
				}*/ else if (eocTaskId) {
					taskId = eocTaskId;
				} else {
					taskId = ndnTaskId;
				}
				$scope.availableMDPTaskId = taskId;
			}
			
			function _bindExportFunction() {
                $scope.exportPlanInfo = function(){
                    if($scope.selectPlan == ''){
                        alert("Please select a plan at least.");
                        return;
                    }
                    FetchDataService.block();
                    var url = "/mcpc/api/hpms/exportPlanInfo?planIds=" + $scope.selectPlan;
                    $.fileDownload(url).done(function(){
                        closeBg();
                    }).fail(function(){
                        closeBg();
                        $scope.layerAlert('Export plan info failed, please try again or contact us.', 'Message');
                    });
                }
                
		        $scope.exportToDocumentLoadingTemplate = function () {
		        	if ($scope.selectPlan == '') {
		            	alert("Please select a plan at least.");
		            	return;
		        	}
		        	FetchDataService.exportToDocumentLoadingTemplate($scope.selectPlan);
		        };
		        
		        $scope.exportAllToDocumentLoadingTemplate = function () {
		        	var currentPage = 0;
		        	var pageSize = 10000000;
					var params = $('#filterForm').serialize().replace('select+state(s)','') + '&currentPage='
							+ currentPage + '&pageSize=' + pageSize + '&pageType='+$scope.pageType;
					if(FetchDataService.validateDate()){
						return;
					}
					FetchDataService.block();
		        	FetchDataService.exportAllToDocumentLoadingTemplate(params);
		        };
		        
		        $scope.exportToEhiTemplate = function () {
		        	if ($scope.selectPlan == '') {
		            	alert("Please select a plan at least.");
		            	return;
		        	}
                    
                    layer.open({
                        type: 1,
                        title: 'Select DB to export',
                        content: $('#exportEhiTemplateModalId'),
                        success: function(layero, index){
                            var loadIndex = layer.load(2);
                            FetchDataService.getAvailablePlanCount($scope.selectPlan).success(function(data){
                                layer.close(loadIndex);
                                $scope.availableMySqlPlanCount = data[0].availableMySqlPlanCount;
                                $scope.availableOraclePlanCount = data[0].availableOraclePlanCount;
                                if($scope.availableMySqlPlanCount == 0 && $scope.availableOraclePlanCount == 0){
                                    layer.close(index);
                                    $scope.layerAlert('No available plan to export.<Br>Please reselect plan(s).', 'Message');
                                    return;
                                }
                                
                                if($scope.availableMySqlPlanCount == 0){
                                    $('#exportMySqlCheckboxId').removeProp("checked").prop("disabled", "disabled");
									if($scope.availableOraclePlanCount == 0){
										$('#exportOracleCheckboxId').removeProp("checked").prop("disabled", "disabled");
									}else{
										$('#exportOracleCheckboxId').prop("checked", true).removeProp("disabled");
									}
                                }else{
                                    $('#exportMySqlCheckboxId').prop("checked", true).removeProp("disabled");
                                    $('#exportOracleCheckboxId').removeProp("checked").prop("disabled", "disabled");
                                }
                            }).error(function(data){
                                layer.close(loadIndex);
                                layer.close(index);
                                $scope.layerAlert('Export EHI benefit template failed, please try again or contact us.', 'Message');
                            });
                        },
                        btn: ['Export', 'Cancel'],
                        yes: function(index, layero){
                            var exportAvailableMySqlPlan = $('#exportMySqlCheckboxId').prop("checked");
                            var exportAvailableOraclePlan = $('#exportOracleCheckboxId').prop("checked");
                            if(!exportAvailableMySqlPlan && !exportAvailableOraclePlan){
                                layer.tips('Please select at least one DB to export.', '.layui-layer-btn0');
                                return;
                            }
                            
                            layer.close(index);
                            FetchDataService.block();
                            var url = "/mcpc/api/hpms/exportPbpToEhiTemplate?planIds=" + $scope.selectPlan + "&exportAvailableMySqlPlan=" + exportAvailableMySqlPlan + "&exportAvailableOraclePlan=" + exportAvailableOraclePlan;
                            $.fileDownload(url).done(function(){
                                closeBg();
                            }).fail(function(){
                                closeBg();
                                $scope.layerAlert('Export EHI benefit template failed, please try again or contact us.', 'Message');
                            });
                        },
                        btn2: function(index, layero){
                            layer.close(index);
                        },
                        end: function(){
                            $scope.restoreExportEhiTemplateModal();
                        }
                    });
		        };
                
                $scope.restoreExportEhiTemplateModal = function(){
                    $scope.availableMySqlPlanCount = 0;
                    $scope.availableOraclePlanCount = 0;
                    $('#exportMySqlCheckboxId').removeProp("checked").removeProp("disabled");
                    $('#exportOracleCheckboxId').removeProp("checked").removeProp("disabled");
                };
                
                $scope.layerAlert = function(content, title){
                    layer.alert(content, {
                        title: title,
                        icon: 0,
                        btn: ['OK']
                    }, function(alertIndex){
                        layer.close(alertIndex);
                    });
                }
                
		        $scope.exportAllToEhiTemplate = function () {
		        	var queryUrl = "";
		        	if ($scope.stateVal != 'select state(s)' && $scope.stateVal != '') {
		        		queryUrl += "selectStates=" + $scope.stateVal + "&";
		        	}
		        	if ($("#carrierName").val() != '') {
		        		queryUrl += "carrierName=" + $("#carrierName").val() + "&";
		        	}
		        	if ($("#planId").val() != '') {
		        		queryUrl += "planId=" + $("#planId").val() + "&";
		        	}
		        	if ($("#planName").val() != '') {
		        		queryUrl += "planName=" + $("#planName").val() + "&";
		        	}
		        	if ($("#loadBy").val() != '') {
		        		queryUrl += "loadBy=" + $("#loadBy").val() + "&";
		        	}
		        	if ($("#startDay").val() != '') {
		        		queryUrl += "startDay=" + $("#startDay").val() + "&";
		        	}
		        	if ($("#endDay").val() != '') {
		        		queryUrl += "endDay=" + $("#endDay").val() + "&";
		        	}
		        	if ($("#jiraNumber").val() != '') {
		        		queryUrl += "jiraNumber=" + $("#jiraNumber").val() + "&";
		        	}
		        	if ($("#fileStatus").val() != '') {
		        		queryUrl += "fileStatus=" + $("#fileStatus").val() + "&";
		        	}
		        	if ($("#planTypeId").val() != '') {
		        		queryUrl += "planTypeId=" + $("#planTypeId").val() + "&";
		        	}
		        	if ($("#productTypeId").val() != '') {
		        		queryUrl += "productTypeId=" + $("#productTypeId").val() + "&";
		        	}
		        	if ($("#specialNeedsPlanTypeId").val() != '') {
		        		queryUrl += "specialNeedsPlanTypeId=" + $("#specialNeedsPlanTypeId").val() + "&";
		        	}
		        	if (queryUrl != '') {
		        		queryUrl = queryUrl.substring(0, queryUrl.length - 1);
		        	}
		        	if (typeof($scope.plans) == 'undefined' || $scope.plans == '' || $scope.plans.length == 0) {
		        		alert("Please search for the plans first.");
		        		return;
		        	}
                    
                    layer.open({
                        type: 1,
                        title: 'Select DB to export',
                        content: $('#exportEhiTemplateModalId'),
                        success: function(layero, index){
                            var loadIndex = layer.load(2);
                            FetchDataService.getAllAvailablePlanCount(queryUrl).success(function(data){
                                layer.close(loadIndex);
                                $scope.availableMySqlPlanCount = data[0].availableMySqlPlanCount;
                                $scope.availableOraclePlanCount = data[0].availableOraclePlanCount;
                                if($scope.availableMySqlPlanCount == 0 && $scope.availableOraclePlanCount == 0){
                                    layer.close(index);
                                    $scope.layerAlert('No available plan to export.<Br>Please try again.', 'Message');
                                    return;
                                }
                                
                                if($scope.availableMySqlPlanCount == 0){
									$('#exportMySqlCheckboxId').removeProp("checked").prop("disabled", "disabled");
									if($scope.availableOraclePlanCount == 0){
										$('#exportOracleCheckboxId').removeProp("checked").prop("disabled", "disabled");
									}else{
										$('#exportOracleCheckboxId').prop("checked", true).removeProp("disabled");
									}
								}else{
									$('#exportMySqlCheckboxId').prop("checked", true).removeProp("disabled");
									$('#exportOracleCheckboxId').removeProp("checked").prop("disabled", "disabled");
								}
                            }).error(function(data){
                                layer.close(loadIndex);
                                layer.close(index);
                                $scope.layerAlert('Export EHI benefit template failed, please try again or contact us.', 'Message');
                            });
                        },
                        btn: ['Export', 'Cancel'],
                        yes: function(index, layero){
                            var exportAvailableMySqlPlan = $('#exportMySqlCheckboxId').prop("checked");
                            var exportAvailableOraclePlan = $('#exportOracleCheckboxId').prop("checked");
                            if(!exportAvailableMySqlPlan && !exportAvailableOraclePlan){
                                layer.tips('Please select at least one DB to export.', '.layui-layer-btn0');
                                return;
                            }
                            
                            layer.close(index);
                            FetchDataService.block();
                            var url = "/mcpc/api/hpms/exportPbpToEhiTemplate?planIds=&exportAvailableMySqlPlan=" + exportAvailableMySqlPlan + "&exportAvailableOraclePlan=" + exportAvailableOraclePlan + "&" + queryUrl;
                            $.fileDownload(url).done(function(){
                                closeBg();
                            }).fail(function(){
                                closeBg();
                                $scope.layerAlert('Export EHI benefit template failed, please try again or contact us.', 'Message');
                            });
                        },
                        btn2: function(index, layero){
                            layer.close(index);
                        },
                        end: function(){
                            $scope.restoreExportEhiTemplateModal();
                        }
                    });
		        };
		        
		        $scope.exportStructuredPlanBenefits = function () {
		        	if ($scope.selectPlan == '') {
		            	alert("Please select a plan at least.");
		            	return;
		        	}		        	
		        	var url = "/mcpc/api/hpms/exportStructuredPlanBenefits?planIds=" + $scope.selectPlan;
		        	FetchDataService.block();
                    $.fileDownload(url).done(function(){
                        closeBg();
                    }).fail(function(){
                        closeBg();
                        $scope.layerAlert('Export Structured Plan Benefits failed, please try again or contact us.', 'Message');
                    });
		        };
		        
		        $scope.exportAllStructuredPlanBenefits = function () {
		        	if (!$scope.structuredPlanBenefits || Object.keys($scope.structuredPlanBenefits).length < 1) {
		        		alert("No search result to export.");
		            	return;
		        	}
		        	var queryUrl = "";
		        	if ($("#cmsContractId").val() != '') {
		        		queryUrl += "cmsContractId=" + $("#cmsContractId").val() + "&";
		        	}
		        	if ($("#cmsPlanId").val() != '') {
		        		queryUrl += "cmsPlanId=" + $("#cmsPlanId").val() + "&";
		        	}
		        	if ($("#cmsSegmentId").val() != '') {
		        		queryUrl += "cmsSegmentId=" + $("#cmsSegmentId").val() + "&";
		        	}
		        	if ($("#planYear").val() != '') {
		        		queryUrl += "planYear=" + $("#planYear").val() + "&";
		        	}
		        	if ($("#startDay").val() != '') {
		        		queryUrl += "startDay=" + $("#startDay").val() + "&";
		        	}
		        	if ($("#endDay").val() != '') {
		        		queryUrl += "endDay=" + $("#endDay").val() + "&";
		        	}
		        	if ($("#jiraNumber").val() != '') {
		        		queryUrl += "jiraNumber=" + $("#jiraNumber").val() + "&";
		        	}
		        	if (queryUrl != '') {
		        		queryUrl = queryUrl.substring(0, queryUrl.length - 1);
		        	}
		        	var url = "/mcpc/api/hpms/exportStructuredPlanBenefits?" + queryUrl;
		        	FetchDataService.block();
                    $.fileDownload(url).done(function(){
                        closeBg();
                    }).fail(function(){
                        closeBg();
                        $scope.layerAlert('Export Structured Plan Benefits failed, please try again or contact us.', 'Message');
                    });
		        };
		    }
			
			$scope.showStructuredPlanBenefits = function (id) {
				var selectedPlan;
				$.each($scope.structuredPlanBenefits, function(index, ele) {
            		if (ele.id == id) {
            			selectedPlan = ele;
            		}
            	});	
				
				$('#structuredPlanBenefitLayer > table > tbody > tr').remove();
				
				$('#structuredPlanBenefitLayer .additional-info').html(
						"<p>CMS Contace Id: " + selectedPlan.cmsContractId + "</p>" +
						"<p>CMS Plan Id: " + selectedPlan.cmsPlanId + "</p>" +
						"<p>CMS Segment Id: " + selectedPlan.cmsSegmentId + "</p>");
				
				$.each(selectedPlan.benefits, function(category, benefits){
					var row = "<tr><td colspan='2' class='benefit-category'>"+ category + "</td></tr>";
					$('#structuredPlanBenefitLayer > table > tbody').append(row);
					$.each(benefits, function(index, benefit){
						var className = "row";
						if ((index + 1) % 2 == 1) {
							className = "row-odd";
						}
						row = "<tr class='" + className + "'><td>" + benefit.name + "</td><td>" + benefit.value + "</td></tr>";
						$('#structuredPlanBenefitLayer > table > tbody').append(row);
					}); 
				});	
				
				/*var template = $('#structuredPlanBenefitLayer');
				$(template).find('table > tbody > tr').remove();
				
				$(template).find('.additional-info').html(
						"<p>CMS Contace Id: " + selectedPlan.cmsContractId + "</p>" +
						"<p>CMS Plan Id: " + selectedPlan.cmsPlanId + "</p>" +
						"<p>CMS Segment Id: " + selectedPlan.cmsSegmentId + "</p>");
				
				$.each(selectedPlan.benefits, function(category, benefits){
					var row = "<tr><td colspan='2' class='benefit-category'>"+ category + "</td></tr>";
					if (benefits && benefits.length > 0) {
						row = "<tr><td rowspan='" + benefits.length + "' class='benefit-category'>"+ category + "</td>";
						row += "<td>" + benefits[0].name + "</td><td>" + benefits[0].value + "</td>"
					}
					$(template).find('table > tbody').append(row);
					$.each(benefits, function(index, benefit){
						if (index > 0) {
							row = "<tr><td>" + benefit.name + "</td><td>" + benefit.value + "</td></tr>";
							$(template).find('table > tbody').append(row);
						}
					}); 
				});	*/
				
				layer.open({
					type: 1,
					area: '700px',
					title:"Structured Plan Benefit",
					content: $('#structuredPlanBenefitLayer')
				});
			}
			
			function _checkIsAllSelected(){
				var flag = true;
				$("input[name='checkName']").each(function() {
					if(this.checked == false){
						flag = false;
					}	
				});
				return flag;
			}
			
			$scope.checkPlan = function(obj){
				if(typeof(obj) != 'object'){
					 obj= eval('(' + obj + ')');
				} else {
					$scope.selectAllPlan = false;
				}
				
				if(_checkIsAllSelected()){
					$scope.selectAllPlan = true;
				}
				
				var id = '#plan-Id-' + obj.planId;
				var key = obj.planId;
				if ($scope.pageType == 'SPB') {
					id = '#plan-Id-' + obj.id;
					key = obj.id;
				}
				var isChecked = $(id).is(':checked');
				if(isChecked){
					$scope.selectPlans.push(key);
				} else {
					$scope.selectPlans.splice(jQuery.inArray(key, $scope.selectPlans), 1); ;
				}
				if($scope.selectPlans.length==0){
					$scope.selectPlan = "";
				}else{
					$scope.selectPlan = $scope.selectPlans.join(",");
				}
			}
			
			$scope.checkAll = function(obj){
				if ($scope.selectAllPlan) {
					$("input[name='checkName']").each(function() {
						if(this.disabled == false && this.checked == false){
							this.checked = true;
							$scope.checkPlan(this.value);
						}
					});
				} else {
					$("input[name='checkName']").each(function() {
						if(this.disabled == false && this.checked == true){
							this.checked = false;
							$scope.checkPlan(this.value);
						}
					});
				}
			}
			
			function _loadSystemDate() {
				 $http.get("/mcpc/api/hpms/getSystemDate").success(function(data){
					 var obj = data[0];
					 var planYear = obj.year;
					 for (var i = planYear; i >= 2014; i--) {
						 var yearOption = "<option value='"+i+"'"; 
						 if (i == planYear) {
							 yearOption += " selected";
						 }
						 yearOption += ">" + i + "</option>";
						 $("#planYear").append(yearOption);
					 }
				});	
			}
			
			$scope.initJiraNums = function(){
				var array = new Array();
				$scope.jiraNumbers = array;
			}
			_loadSystemDate();
			_loadName();
			_initPageType();
			_bindExportFunction();
		} ]);

historyModule.factory("FetchDataService", ["$http", function($http) {
	var post = function(business) {
		return $http.post('/mcpc/api/hpms/getPlans', business, {
			transformRequest : angular.identity,
            transformResponse: prefixTransform($http.defaults.transformResponse, function(value){
                return _.unescape(value);
            }),
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			}
		});
	};

	var total = function(params) {
		return $http.post('/mcpc/api/hpms/getPlansTotalNum', params, {
			transformRequest : angular.identity,
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			}
		});
	};
	
	var exportAllToDocumentLoadingTemplate = function(params) {
		return $http.post('/mcpc/api/hpms/exportAllToDocumentLoadingTemplate', params, {
			transformRequest : angular.identity,
            transformResponse: prefixTransform($http.defaults.transformResponse, function(data){
            	var blob = new Blob([data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
            	var filename = "Document_Loading_Template " + new Date().format("yyyy-MM-dd hh-mm-ss");
        	    const elink = document.createElement('a');
                elink.download = filename;
                elink.style.display = 'none';
                elink.href = URL.createObjectURL(blob);
                document.body.appendChild(elink);
                elink.click();
                URL.revokeObjectURL(elink.href);
                document.body.removeChild(elink);
        	    closeBg();
            }),
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			},
			responseType: 'arraybuffer'
		});
	};

    return{
        post : function(business) {
            return post(business);
        },

        total : function(params) {
            return total(params);
        },

        validateDate : function(){
            var startDay = new Date($('.form-date-start').val());
            var endDay = new Date($('.form-date-end').val());

            if (startDay > endDay) {
                alert('Invalid search criteria: Loaded between <' + startDay.toLocaleDateString() + '> and <' + endDay.toLocaleDateString() + '>, the end date should be later that start date.');
                return true;
            }
            return false;
        },

        block: function(){
            showBg('dialog', 'dialog_content');   
        },

        buildJiraLink:function(val){
            return jiraLocation + val;
        },

        getPageType : function() {
            var url = location.search;
            var theRequest = new Object();
            if (url.indexOf("?") != -1) {
                var str = url.substr(1);
                strs = str.split("&");
                for (var i = 0; i < strs.length; i++) {
                    theRequest[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
                }
            }
            return theRequest.pageType;
        },
        exportToDocumentLoadingTemplate : function(planIds){
            return window.location = "/mcpc/api/hpms/exportToDocumentLoadingTemplate?planIds=" + planIds;
        },
        getAvailablePlanCount: function(planIds){
            return $http.get("/mcpc/api/hpms/getAvailablePlanCount?planIds=" + planIds);
        },
        getAllAvailablePlanCount: function(queryUrl){
            return $http.get("/mcpc/api/hpms/getAllAvailablePlanCount?planIds=&" + queryUrl);
        },
        exportAllToDocumentLoadingTemplate : function(params) {
            return exportAllToDocumentLoadingTemplate(params);
        }
    };
}]);

historyModule.filter('to_trusted', [ '$sce', function($sce) {
	return function(text) {
		return $sce.trustAsHtml(text);
	}
} ]);

function prefixTransform(defaults, transform){
    transform = [transform];
    defaults = angular.isArray(defaults) ? defaults : [defaults];
    return transform.concat(defaults);
};

Date.prototype.format = function(fmt) { 
     var o = { 
        "M+" : this.getMonth()+1,
        "d+" : this.getDate(),
        "h+" : this.getHours(),
        "m+" : this.getMinutes(),
        "s+" : this.getSeconds(),
        "q+" : Math.floor((this.getMonth()+3)/3),
        "S"  : this.getMilliseconds() 
    }; 
    if(/(y+)/.test(fmt)) {
            fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
    }
     for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
             fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
         }
     }
    return fmt; 
} 