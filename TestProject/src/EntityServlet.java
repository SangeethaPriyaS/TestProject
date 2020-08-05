/* VERSION:  |S11.04.0009| TIME 15-Apr-2020 10:43:05 FILE: EntityServlet.java DEV:29171 REL:29184 */

/* VERSION:  |11.02| TIME 16-Dec-2016 09:15:39 FILE: EntityServlet.java DEV:6065 REL:6108 */
/* VERSION:  |11.01| TIME 18-Sep-2013 01:25:39 FILE: EntityServlet.java DEV:6065 REL:6108 */

/* ***** Development File Details:$Rev: 29171 $, $Author: Haroon $, $Date: 2020-04-14 04:24:59 -0400 (Tue, 14 Apr 2020) $ ***** Added by Program */
/* VERSION: 10.5.0.995 TIME: 11-Aug-2010 09:03:09 FILE: EntityServlet.java */
/**
 * Project 		 	: IBS 
 * Screen Name   	: Entity Maintenance 
 * Description   	: This form is used to maintain the Entity information 
 * Author 			: E1230
 * Date 			: 28-May-2007 
 * Screen By 	 	: Softeon 
 * Modified by   	: 
 * Modified Date	:  
 * Modified Reason  : 
 */

package epm;

import java.io.IOException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import epm.accs.ValidateAccs;
import epm.common.ReturnStatus;
import epm.dbutil.DbManager;
import epm.entity.EntityDBObject;
import epm.entity.EntityRemoteProcess;
import epm.entity.EntityRequest;
import epm.entity.EntityResponse;
import epm.gl.GLAccount;
import epm.sysparm.IBSSysParamDataStore;
import epm.sysparm.IBSSystemParameter;
import epm.util.AppSession;
import epm.util.Constant;
import epm.util.Logger;
import epm.util.PhoneNumber;
import epm.util.STKGeneral;
import epm.util.ServiceConstant;
import epm.util.TableConstant;
import epm.util.TableConstants;
import epm.util.UserRequest;


/*  
 * 
 * ===================================Change History : =========================================
 * ----------------------------------------------------------------------------------------------------------
 * Change#	 Release# 		Date 		 By 				Description
 * ----------------------------------------------------------------------------------------------------------
 *    -		    -		  28-May-2007    E1230 	    	Entity Maintenance 
 * 	  1			14		  13-Jul-2007	 E1230			Javadoc
 *    2         15        05-May-2011    Chinuk         Added. GL Close Thru year(GLOPENYear)
 */

public class EntityServlet extends EntPropSecurity 
{
	
	public String m_SVNDevelopmentDetails = "Development File Details:$Rev: 29171 $, $Author: Haroon $, $Date: 2020-04-14 04:24:59 -0400 (Tue, 14 Apr 2020) $";
 public String _VERSION_ = "|VERSION| |S11.04.0009|TIME|15-Apr-2020 10:43:05|DEV|29171|REL|29184";

   
	private final String	SERVLET_NAME	= "EntityServlet : ";
	
	public static final String CALL_PROGRAM 	= "EntityServlet";
    public static final String MENU_PARAM1_SENT = "SUPER_ENTITY";
    public static final String MENU_PARAM1_ENT  = "ENTITY";
    public static final String MENU_PARAM1_PROP = "PROPERTY";
	
	private final String TBLSYSPARAM_93		= "93";
	
	public final String ENTITY_TYPE   		= "ENTITY_TYPE";
	public final String ENTITY_FREQ  		= "ENTITY_FREQ";
	public final String ENTITY_MONTHS 		= "MONTHS";
	
	public final String STANDALONE 			= "S";
	public final String FULL_AGENCY 		= "F";
	public final String AGENCY_RECEPTS 		= "R";
	public final String AGENCY_DISB 		= "D";
	public final String NO_CASH_AGENCY 		= "N";
	
	public final String UNDELETE_MODE 		= "C";
	private final String TBLSYSPARAM_397	= "397";
	private final String TBLSYSPARAM_89     = "89";
	private final String TBLSYSPARAM_585    = "585";
	private final String TBLSYSPARAM_586    = "586";
	
	private static final String CHECK_ENTITY_PROPERTY_RIGHTS  = "CHECK_ENTITY_PROPERTY_RIGHTS";
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		HttpSession session = null;
		session = request.getSession(true);
		AppSession appSession = new AppSession(session);
		
		String module = STKGeneral.nullCheck(request.getParameter("Module"));
		String subModule = STKGeneral.nullCheck(request.getParameter("subModule"));
		
		if(module.trim().equalsIgnoreCase("AJAX"))
		{
			if(subModule.trim().equalsIgnoreCase(CHECK_ENTITY_PROPERTY_RIGHTS))
			{
				String sEntId = "";
				String sPropId = "";
				String entProp = "";
				
				entProp = STKGeneral.nullCheck(request.getParameter("entProp"));
				
				if(entProp != null && entProp.trim().equalsIgnoreCase("P"))
					sPropId = STKGeneral.nullCheck(request.getParameter("txtEntProp")).toUpperCase();
				else if(entProp != null && entProp.trim().equalsIgnoreCase("E"))
					sEntId = STKGeneral.nullCheck(request.getParameter("txtEntProp"));
				
				try
				{
					Vector resultMsg = getEntPropAccessRightMsg(sEntId,sPropId,appSession,session);
					
					String result = "";
					String entView = "";
					String entModify = "";
					String entDelete = "";
					
					String propView = "";
					String propModify = "";
					String propDelete = "";
					
					if(resultMsg != null && resultMsg.size() > 0)
					{
						entView = STKGeneral.nullCheck((String) resultMsg.elementAt(1)).trim();
						entModify = STKGeneral.nullCheck((String) resultMsg.elementAt(2)).trim();
						entDelete = STKGeneral.nullCheck((String) resultMsg.elementAt(3)).trim();
						
						propView = STKGeneral.nullCheck((String) resultMsg.elementAt(4)).trim();
						propModify = STKGeneral.nullCheck((String) resultMsg.elementAt(5)).trim();
						propDelete = STKGeneral.nullCheck((String) resultMsg.elementAt(6)).trim();
					}
					
					result = "<DATA>ACCESSRIGHTSVALUE</DATA>"
						+"<ENTVIEW>" + STKGeneral.nullCheck(entView) + "</ENTVIEW>"
						+"<ENTMODIFY>" + STKGeneral.nullCheck(entModify) + "</ENTMODIFY>"
						+"<ENTDELETE>" + STKGeneral.nullCheck(entDelete) + "</ENTDELETE>"
						+"<PROPVIEW>" + STKGeneral.nullCheck(propView) + "</PROPVIEW>"
						+"<PROPMODIFY>" + STKGeneral.nullCheck(propModify) + "</PROPMODIFY>"
						+"<PROPDELETE>" + STKGeneral.nullCheck(propDelete) + "</PROPDELETE>";
					
					response.getOutputStream().write((result).getBytes());
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(subModule.trim().equalsIgnoreCase("supEntAttach"))
			{
				String result = "";
				try
				{			
					String cntlValue = STKGeneral.nullCheck(request.getParameter("cntlValue"));	
					result = getDesc(request,appSession,cntlValue,true);
					response.getOutputStream().write((result).getBytes());
				}
				catch(Exception e)
				{
					Logger.log(new UserRequest(), SERVLET_NAME, "@@supEntAttach_result_err : " , e, Logger.ERROR, Logger.LOG_COMMON);
					e.printStackTrace();
				}
			}
			else if(subModule.trim().equalsIgnoreCase("entRequest"))
			{
				String result = "";
				try
				{	
					String cntlValue = STKGeneral.nullCheck(request.getParameter("cntlValue"));	
					result = getDesc(request,appSession,cntlValue,false);
					response.getOutputStream().write((result).getBytes());
				}
				catch(Exception e)
				{
					Logger.log(new UserRequest(), SERVLET_NAME, "@@entRequest_result_err : " , e, Logger.ERROR, Logger.LOG_COMMON);
					e.printStackTrace();
				}
			}
			else if(subModule.trim().equalsIgnoreCase("GetZipValid"))
			{
				String result = "";
				try
				{							
					result = zipCodeValidation(request ,appSession);
					response.getOutputStream().write((result).getBytes());
				}
				catch(Exception e)
				{
					Logger.log(new UserRequest(), SERVLET_NAME, "@@entRequest_result_err : " , e, Logger.ERROR, Logger.LOG_COMMON);
					e.printStackTrace();
				}
			}
			else
			{
				String result = "";
				try
				{			
					String cntlValue = STKGeneral.nullCheck(request.getParameter("cntlValue"));	
					result = getFiscalMnthValue(request,appSession,cntlValue);	
					response.getOutputStream().write((result).getBytes());
				}
				catch(Exception e)
				{
					Logger.log(new UserRequest(), SERVLET_NAME, "@@Fiscal_result_err : " , e, Logger.ERROR, Logger.LOG_COMMON);
					e.printStackTrace();
				}
			}
		}
		else
		{		
			try
			{
				super.service(request, response);
			} 
			catch(Exception e)
			{
				return;
			}
			
//			Connection conn = null;
			String sUserId = "";
			String sError = "";
			String sSourceOfError = "";
			String poolName = "";
			String selPoolName = "";
			
			String sysKey1 = appSession.getSysKey1();
			sUserId = (String) session.getAttribute(Constant.SESSION_USER_ID);
			UserRequest userReq = appSession.getUserRequest();
			String processId = appSession.getProcessID();
			poolName = appSession.getJDBCPoolName();
			request.setAttribute("sServerError", Constant.CHAR_NO);
				
			try
			{
				String currentTab = "";
				String dbMode = Constant.NO_MODE;
				String entId = "";
				String entName = "";
				String layOutValue = "";
				EntityResponse entityResp   = null;
				String tblSysParamValue397  = "";
				String tblSysParamValue89   = "" ;
				String tblSysParamValue585  = "";
				String tblSysParamValue586  = "";
				String entView = "";
				String entModify = "";
				String entDelete = "";
				String propView = "";
				String propModify = "";
				String propDelete = "";
				
				String paramValue = "";
				Vector resultVect ;
				
				String supEntMenuId = getMenuId(appSession,poolName,sysKey1,CALL_PROGRAM,MENU_PARAM1_SENT);
				String entMenuId = getMenuId(appSession,poolName,sysKey1,CALL_PROGRAM,MENU_PARAM1_ENT);
				
				IBSSysParamDataStore sysParmDS = new IBSSysParamDataStore(entMenuId, appSession, request);
				sysParmDS.setSysParamCollection();
				Hashtable sysParamColl = sysParmDS.getFieldCollection();
				
				Logger.log(userReq, SERVLET_NAME, "@@sysParamColl : " , sysParamColl, Logger.INFO, Logger.LOG_COMMON);

				layOutValue		= STKGeneral.nullCheck(getSysParmValue(sysParamColl, TBLSYSPARAM_93));
				Logger.log(userReq, SERVLET_NAME, "@@layOutValue : " , layOutValue, Logger.INFO, Logger.LOG_COMMON);
				request.setAttribute("layOutValue",layOutValue);
				
				IBSSysParamDataStore sysParmDataStore = new IBSSysParamDataStore(supEntMenuId, appSession, request);
				sysParmDataStore.setSysParamCollection();
				Hashtable sysParamCollect = sysParmDS.getFieldCollection();
				Logger.log(userReq, SERVLET_NAME, "@@sysParamCollect : " , sysParamCollect, Logger.INFO, Logger.LOG_COMMON);

				
				tblSysParamValue397		= STKGeneral.nullCheck(getSysParmValue(sysParamCollect, TBLSYSPARAM_397));
				request.setAttribute("tblSysParamValue397", tblSysParamValue397);
				Logger.log(userReq, SERVLET_NAME, "@@tblSysParamValue397 : " , tblSysParamValue397, Logger.INFO, Logger.LOG_COMMON);

				tblSysParamValue89 = STKGeneral.nullCheck(getSysParmValue(sysParamCollect, TBLSYSPARAM_89));
				request.setAttribute("tblSysParamValue89", tblSysParamValue89);
				Logger.log(userReq, SERVLET_NAME, "@@tblSysParamValue89 : " , tblSysParamValue89, Logger.INFO, Logger.LOG_COMMON);

				
				tblSysParamValue585 = STKGeneral.nullCheck(getSysParmValue(sysParamCollect, TBLSYSPARAM_585));
				String month = "";
				String year = "";
				if (tblSysParamValue585.length() > 1)
				{
					month = tblSysParamValue585.substring(0,2);
				}
				if (tblSysParamValue585.length() > 4)
				{
					year = tblSysParamValue585.substring(5);
				}
				request.setAttribute("tblSysParamValue585", month+year);
				Logger.log(userReq, SERVLET_NAME, "@@tblSysParamValue585 : " , tblSysParamValue585, Logger.INFO, Logger.LOG_COMMON);
				
				
				tblSysParamValue586 = STKGeneral.nullCheck(getSysParmValue(sysParamCollect, TBLSYSPARAM_586));
				
				if (tblSysParamValue586.length() > 1)
				{
					month = tblSysParamValue586.substring(0,2);
				}
				if (tblSysParamValue586.length() > 4)
				{
				 year = tblSysParamValue586.substring(5);
				}
				request.setAttribute("tblSysParamValue586", month+year);
				Logger.log(new UserRequest(), SERVLET_NAME, "@@tblSysParamValue586 : ", month+year,Logger.INFO, Logger.LOG_COMMON);

				if(tblSysParamValue397.trim().equals("N"))
				{
				    /*StringBuffer resBuf = new StringBuffer("");
					String result = "";
					
					String sql = " SELECT  PARAM_VALUE FROM TBLSYSPARAM"
							+" WHERE SYS_KEY1 = '" + appSession.getSysKey1()+"'"
							+" AND FIELDNUM = 585";
					
					Logger.log(new UserRequest(), SERVLET_NAME, "@@sql : "+sql,Logger.INFO, Logger.LOG_COMMON);

					resultVect = DbManager.getQueryResult(new Vector(), sql, appSession.getJDBCPoolName());

					Logger.log(new UserRequest(), SERVLET_NAME, "@@resultVect : "+resultVect,Logger.INFO, Logger.LOG_COMMON);

					if(resultVect != null && resultVect.size() > 0)
					{
					    	Vector innerVect = (Vector) resultVect.elementAt(0);
							paramValue = (String) innerVect.elementAt(0);
							Logger.log(new UserRequest(), SERVLET_NAME, "@@@@@@paramValue : "+paramValue,Logger.INFO, Logger.LOG_COMMON);

					}*/
				    //month = tblSysParamValue585.substring(0,2);
					//year = tblSysParamValue585.substring(5);
					if (tblSysParamValue585.length() > 1)
					{
						month = tblSysParamValue585.substring(0,2);
					}
					if (tblSysParamValue585.length() > 4)
					{
						year = tblSysParamValue585.substring(5);
					}
					Logger.log(new UserRequest(), SERVLET_NAME, "@@paramValue date: ", month+year,Logger.INFO, Logger.LOG_COMMON);

					request.setAttribute("paramValue", month+year);
					paramValue = STKGeneral.nullCheck(request.getParameter("hParamValue"));
				}
					
				//request.setAttribute("paramValue", paramValue);
				//paramValue = STKGeneral.nullCheck(request.getParameter("hParamValue"));
				
				Logger.log(new UserRequest(), SERVLET_NAME, "@@paramValue : "+paramValue, paramValue,Logger.INFO, Logger.LOG_COMMON);
				
				Vector entityType = getCodeDetails(ENTITY_TYPE,appSession);
				Vector entityFreq = getCodeDetails(ENTITY_FREQ,appSession);
				Vector entityMnth = getCodeDetails(ENTITY_MONTHS,appSession);
				request.setAttribute("entityType",entityType);
				request.setAttribute("entityFreq",entityFreq);
				request.setAttribute("entityMnth",entityMnth);
				
				Vector propertyStatusVector = comboVector(appSession,Constant.PROPERTY_STATUS);
	            Vector printInvoiceVector = comboVector(appSession,Constant.PRINT_INVOICE);
	            Vector salesTaxVector = comboVector(appSession,Constant.SALES_TAX);
	            Vector feeTableVector = comboVector(appSession,Constant.FEE_TABLE);
	            Vector arModsJeVector = comboVector(appSession,Constant.ARMODS_JE);
	            
	            request.setAttribute("propertyStatus", propertyStatusVector);
	            request.setAttribute("printInvoice", printInvoiceVector);
	            request.setAttribute("salesTax", salesTaxVector);
	            request.setAttribute("feeTable", feeTableVector);
	            request.setAttribute("arModsJe", arModsJeVector);
				
				currentTab = STKGeneral.nullCheck((String)request.getParameter("hCurrentTab")).trim();
				Logger.log(userReq, SERVLET_NAME, "@@currentTab : " , currentTab, Logger.INFO, Logger.LOG_COMMON);
				
				if(currentTab.equalsIgnoreCase(""))
					currentTab = STKGeneral.nullCheck((String)request.getParameter("hPARAM1")).trim();
				
				Logger.log(userReq, SERVLET_NAME, "@@dbMode_Entity11 : " , dbMode, Logger.INFO, Logger.LOG_COMMON);
				dbMode = STKGeneral.nullCheck((String)request.getParameter("hDBMode"));
				
				if(dbMode.trim().equals(""))
					dbMode = Constant.NO_MODE;
				Logger.log(userReq, SERVLET_NAME, "@@dbMode_Entity : " , dbMode, Logger.INFO, Logger.LOG_COMMON);
				
				//if(!dbMode.trim().equalsIgnoreCase(Constant.NO_MODE))
				entityResp = getEntityValues(request,dbMode);
				Logger.log(userReq, SERVLET_NAME, "@@@@@entityResp : " , entityResp, Logger.INFO, Logger.LOG_COMMON);
				
				request.setAttribute("currentTab",currentTab);
				
				entView = STKGeneral.nullCheck((String)request.getParameter("hEntView")).trim();
				entModify = STKGeneral.nullCheck((String)request.getParameter("hEntModify")).trim();
				entDelete = STKGeneral.nullCheck((String)request.getParameter("hEntDelete")).trim();
				request.setAttribute("entView",entView);
				request.setAttribute("entModify",entModify);
				request.setAttribute("entDelete",entDelete);
				
				request.setAttribute("screenMenuId",entMenuId);
				EntityResponse entRsp = null;
				
				if(dbMode.equals(Constant.INSERT_MODE))		// For Add call in the Entity tab - Create new
				{
					Vector addEntVect = new Vector();
					EntityResponse addEntRes = new EntityResponse();
					EntityRequest entRequest 		= new EntityRequest();
					entRequest.setRequestType(ServiceConstant.ADD_ENTITY);
					entRequest.setProcessID(STKGeneral.getInteger(processId));
					Logger.log(userReq, SERVLET_NAME, "@@Ent_Id : " ,entityResp.getEntID(), Logger.INFO, Logger.LOG_COMMON);
					entRequest.setEntityID(entityResp.getEntID());
					
					EntityRemoteProcess entitytRemote =  new EntityRemoteProcess(appSession, request,entRequest,entityResp);
			
					Vector respVector = entitytRemote.doInsert();
					Logger.log(userReq, SERVLET_NAME, "@@respVector_Insert : " ,respVector, Logger.INFO, Logger.LOG_COMMON);
					if(respVector != null && respVector.size() > 0)
					{
						ReturnStatus retStatus = (ReturnStatus)respVector.elementAt(1);
						addEntRes	= (EntityResponse)respVector.elementAt(0);
						
						if( retStatus != null && retStatus.isSucces())
						{
							EntityDBObject entDBObj = new EntityDBObject();
							entDBObj.setResponseData(addEntRes);
							EntityDBProcess entDBProcess = new EntityDBProcess(entDBObj,appSession);
							entDBProcess.doDBInsert();
							
							entRsp = addEntRes;
							dbMode = Constant.UPDATE_MODE;
						}
						else
						{
							dbMode = Constant.INSERT_MODE;
						}
					}
					else
					{
						dbMode = Constant.INSERT_MODE;
					}
				}
				else if(dbMode.equals(Constant.UPDATE_MODE))	// For Update call in the Entity Tab - Update the existing Entity
				{
					EntityResponse updEntRes = new EntityResponse();
					EntityRequest entRequest 		= new EntityRequest();
					entRequest.setRequestType(ServiceConstant.UPDATE_ENTITY);
					entRequest.setProcessID(STKGeneral.getInteger(processId));
					entRequest.setEntityID(entityResp.getEntID());
					
					Logger.log(userReq, SERVLET_NAME, "@@respVector_Update_entityResp : " ,entityResp, Logger.INFO, Logger.LOG_COMMON);
					EntityRemoteProcess entitytRemote =  new EntityRemoteProcess(appSession, request,entRequest,entityResp);
					Vector respVector = entitytRemote.doUpdate();
					Logger.log(userReq, SERVLET_NAME, "@@respVector_Update : " ,respVector, Logger.INFO, Logger.LOG_COMMON);
					if(respVector != null && respVector.size() > 0)
					{
						ReturnStatus retStatus = (ReturnStatus)respVector.elementAt(1);
						updEntRes	= (EntityResponse)respVector.elementAt(0);
						
						if( retStatus != null && retStatus.isSucces())
						{
							EntityDBObject entDBObj = new EntityDBObject();
							entDBObj.setResponseData(updEntRes);
							EntityDBProcess entDBProcess = new EntityDBProcess(entDBObj,appSession);
							entDBProcess.doDBUpdate();
							
							entRsp = updEntRes;
						}
					}
					dbMode = Constant.UPDATE_MODE;
				}
				else if(dbMode.equals(Constant.DELETE_MODE))	// For Delete call in the Entity Tab - Delete the existing Entity
				{
					EntityResponse delEntRes = new EntityResponse();
					EntityRequest entRequest 		= new EntityRequest();
					entRequest.setRequestType(ServiceConstant.DELETE_ENTITY);
					entRequest.setProcessID(STKGeneral.getInteger(processId));
					entRequest.setEntityID(entityResp.getEntID());
					EntityRemoteProcess entitytRemote =  new EntityRemoteProcess(appSession, request,entRequest,entityResp);
					Vector respVector = entitytRemote.doDelete();
					Logger.log(userReq, SERVLET_NAME, "@@respVector_delete : " ,respVector, Logger.INFO, Logger.LOG_COMMON);
					if(respVector != null && respVector.size() > 0)
					{
						ReturnStatus retStatus = (ReturnStatus)respVector.elementAt(1);
						delEntRes	= (EntityResponse)respVector.elementAt(0);
						
						if( retStatus != null && retStatus.isSucces())
						{
							EntityDBObject entDBObj = new EntityDBObject();
							entDBObj.setResponseData(delEntRes);
							EntityDBProcess entDBProcess = new EntityDBProcess(entDBObj,appSession);
							entDBProcess.doDBDelete();
							
							entRsp = delEntRes;
							dbMode = Constant.DELETE_MODE;
						}
						else
						{
							dbMode = Constant.UPDATE_MODE;
						}
					}
					else
					{
						dbMode = Constant.UPDATE_MODE;
					}
				}
				else if(dbMode.equals(UNDELETE_MODE))	// For Undelete call in the Entity Tab - Undelete the existing Entity
				{
					EntityResponse unDelEntRes = new EntityResponse();
					EntityRequest entRequest 		= new EntityRequest();
					entRequest.setRequestType(ServiceConstant.UNDELETE_ENTITY);
					entRequest.setProcessID(STKGeneral.getInteger(processId));
					entRequest.setEntityID(entityResp.getEntID());
					EntityRemoteProcess entitytRemote =  new EntityRemoteProcess(appSession, request,entRequest,entityResp);
					Vector respVector = entitytRemote.doUnDelete();
					
					Logger.log(userReq, SERVLET_NAME, "@@respVector_Undelete : " ,respVector, Logger.INFO, Logger.LOG_COMMON);
					if(respVector != null && respVector.size() > 0)
					{
						ReturnStatus retStatus = (ReturnStatus)respVector.elementAt(1);
						unDelEntRes	= (EntityResponse)respVector.elementAt(0);
						
						if( retStatus != null && retStatus.isSucces())
						{
							EntityDBObject entDBObj = new EntityDBObject();
							entDBObj.setResponseData(unDelEntRes);
							EntityDBProcess entDBProcess = new EntityDBProcess(entDBObj,appSession);
							entDBProcess.doDBDelete();
							
							entRsp = unDelEntRes;
							dbMode = Constant.UPDATE_MODE; 
						}
						else
						{
							dbMode = Constant.DELETE_MODE; 
						}
					}
					else
					{
						dbMode = Constant.DELETE_MODE; 
					} 
				}
				else if(dbMode.equals("G"))		// For Get call in the Entity Tab. 
				{
					EntityResponse getEntRes = new EntityResponse();
					EntityRequest entRequest 		= new EntityRequest();
					entRequest.setRequestType(ServiceConstant.GET_ENTITY);
					entRequest.setProcessID(STKGeneral.getInteger(processId));
					entRequest.setEntityID(entityResp.getEntID());
					EntityRemoteProcess entitytRemote =  new EntityRemoteProcess(appSession, request,entRequest,entityResp);
					Vector respVector = entitytRemote.doGet();
					Logger.log(userReq, SERVLET_NAME, "@@respVector_Get : " ,respVector, Logger.INFO, Logger.LOG_COMMON);
					if(respVector != null && respVector.size() > 0)
					{
						ReturnStatus retStatus = (ReturnStatus)respVector.elementAt(1);
						boolean delFlag = false;
						
						if( retStatus != null && retStatus.isSucces())
						{
							getEntRes	= (EntityResponse)respVector.elementAt(0);
							
							delFlag = getEntRes.isDeleted();
							
							if(delFlag)
							{
								dbMode = Constant.DELETE_MODE;
							}
							else
							{
								dbMode = Constant.UPDATE_MODE;
							}
							
							entRsp = getEntRes;
						}
						else
						{
							dbMode = Constant.NO_MODE;
						}
					}
					else
					{
						dbMode = Constant.NO_MODE;
					}
				}
				
				Logger.log(userReq, SERVLET_NAME, "@@Final_entRsp : " ,entRsp, Logger.INFO, Logger.LOG_COMMON);
				
				if(entRsp != null)		// && !entRsp.equals(""))
				{
					request.setAttribute("entityResp",entRsp);
				}
				else
				{
					request.setAttribute("entityResp",entityResp);
				}
				
				request.setAttribute("dbMode",dbMode); 
				String newAbsCall = STKGeneral.nullCheck((String) request.getParameter("hNewAbsCall")).trim();
				
				if(newAbsCall.trim().equals(""))
					request.setAttribute("newAbsCall",AbstractGeneral.getSysParamValue(sysKey1,poolName,Constant.WEB_BASED_ABSTRACT));
				else
					request.setAttribute("newAbsCall",newAbsCall);
			}
			catch(RemoteException re)
			{
				if (re.getMessage().indexOf(Constant.TIMEOUT_EXCEPTION) >= 0)
				{
					request.setAttribute("timeOutException",Constant.CHAR_YES);	
				}
				else
				{
					String sException = re.toString();
					sSourceOfError = SERVLET_NAME;
					Logger.log(	userReq, SERVLET_NAME, "service()-->EXCEPTION : " + sException, re, Logger.ERROR, Logger.LOG_COMMON);
					request.setAttribute("sSourceOfError",URLEncoder.encode (SERVLET_NAME + sSourceOfError));
					request.setAttribute("sException",URLEncoder.encode (sException));
				}
			}
			catch(Exception e)
			{
				String sException = e.toString();
				Logger.log(SERVLET_NAME, sException, e, Logger.ERROR, Logger.LOG_COMMON);
				request.setAttribute("sSourceOfError", URLEncoder.encode(SERVLET_NAME + sSourceOfError));
				request.setAttribute("sException", URLEncoder.encode(sException));
			}
			finally
			{
				//calling the corresponding JSP file and passing the request and response objects to the JSP
				//getServletContext will be the ServletContext for the current ServletConfig.
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(Constant.BASE_URL + "/EntityProperty.jsp");
				dispatcher.forward(request, response);
			}
		}
	} 
	
	/**
	 * This method is used to set the control values in the Entity Response
	 * @param request			HttpServletRequest
	 * @return EntityResponse
	 * @throws Exception
	 */	
	public EntityResponse getEntityValues(HttpServletRequest request,String aDBMode) throws Exception
	{
		EntityResponse entResp = new EntityResponse();
		
		try
		{	
			String entName 			= STKGeneral.nullCheck(request.getParameter("txtEntName")).trim();
			String entId 			= STKGeneral.nullCheck(request.getParameter("txtEntNo")).trim();
			String FedId 			= STKGeneral.nullCheck(request.getParameter("txtFedId")).trim();
			String FiscYrStrtMnth 	= STKGeneral.nullCheck(request.getParameter("hFiscYrStrtMnth")).trim();
			String GLOPENyear       = STKGeneral.nullCheck(request.getParameter("txtGLOPENyear")).trim();
			String Address 			= STKGeneral.nullCheck(request.getParameter("txtAddress")).trim();
			String City 			= STKGeneral.nullCheck(request.getParameter("txtCity")).trim();
			String State 			= STKGeneral.nullCheck(request.getParameter("txtState")).trim();
			String Zip 				= STKGeneral.nullCheck(request.getParameter("txtZip")).trim();
			String Telephone 		= STKGeneral.nullCheck(request.getParameter("txtTelephone")).trim();
			String EntType 			= STKGeneral.nullCheck(request.getParameter("hEntType")).trim();
			request.setAttribute("EntType",EntType);
			String SupEntNo 		= STKGeneral.nullCheck(request.getParameter("txtSupEntNo")).trim();
			String OwningAgencyNo 	= STKGeneral.nullCheck(request.getParameter("txtOwningAgencyNo")).trim();
			String Operating 		= STKGeneral.nullCheck(request.getParameter("txtOperating")).trim();			
			String Security 		= STKGeneral.nullCheck(request.getParameter("txtSecurity")).trim();
			String CashReceiptsGL 	= STKGeneral.nullCheck(request.getParameter("txtCashReceiptsGL")).trim();
			//String CashDisbGL 		= STKGeneral.nullCheck(request.getParameter("txtCashDisbGL")).trim();
			String EntIntercoGL 	= STKGeneral.nullCheck(request.getParameter("txtEntIntercoGL")).trim();
			String AgencyIntercoGL 	= STKGeneral.nullCheck(request.getParameter("txtAgencyIntercoGL")).trim();			
			String TenRecvblGL 		= STKGeneral.nullCheck(request.getParameter("txtTenRecvblGL")).trim();
			String APGL 			= STKGeneral.nullCheck(request.getParameter("txtAPGL")).trim();
			String DistributionAmt 	= STKGeneral.nullCheck(request.getParameter("txtDistribution")).trim();
			String DistributionFreq = STKGeneral.nullCheck(request.getParameter("hDistributionFreq")).trim();
			String StartMnth 		= STKGeneral.nullCheck(request.getParameter("hStartMnth")).trim();
			String chkPBIFile 		= STKGeneral.nullCheck(request.getParameter("hChkPBIFile")).trim();
			String chkDeleted 		= STKGeneral.nullCheck(request.getParameter("hChkDeleted")).trim();
			
			entResp.setName(STKGeneral.getPadString(entName.toUpperCase(),Constant.SPACE_PAD_STR,30,Constant.RIGHT_PAD));
			entResp.setEntID(STKGeneral.getInteger(entId));
			

			if(FedId.indexOf("-") > -1)
				FedId = STKGeneral.replaceString(FedId,"-","");
			if(FedId.length() > 0)
				entResp.setGovtID(STKGeneral.getInteger(FedId));
			else
				entResp.setGovtID(0);
			
			entResp.setFiscalStartMonth(STKGeneral.getInteger(FiscYrStrtMnth));
			
			
		
			
			
			
			int lengthGLOPENyear = GLOPENyear.length();
			
			//Added 05-May-2011
			//Currently keeps GLOPENyear as integer type at EntityResponse class, but uses String type at EntityDBObject class
			//YYYY year format is used on Entity.jsp entry, but stores in database as two-digit char format, YY.
			if (GLOPENyear.trim().equals("N/A") ||  GLOPENyear.trim().equals("") || GLOPENyear.trim().equals(null))
			{
			  entResp.setGLOPENyear(9999);
			} else
			{
			  entResp.setGLOPENyear(Integer.parseInt(GLOPENyear.substring(lengthGLOPENyear - 2, lengthGLOPENyear)));
			}
			
		   
			
			if(Address.length() > 0)
				entResp.setAddress(STKGeneral.getPadString(Address.toUpperCase(),Constant.SPACE_PAD_STR,27,Constant.RIGHT_PAD));
			else
				entResp.setAddress("");
			
			if(City.length() > 0)
				entResp.setCity(STKGeneral.getPadString(City.toUpperCase(),Constant.SPACE_PAD_STR,16,Constant.RIGHT_PAD));
			else
				entResp.setCity("");
			
			entResp.setState(State.toUpperCase());
			
			if(Zip.indexOf("-") > -1)
				Zip = STKGeneral.replaceString(Zip,"-","");
			
			if(Zip.length() > 0)
				entResp.setZipCode(STKGeneral.getInteger(Zip));
			else
				entResp.setZipCode(0);
						
			//Logger.log(new UserRequest(), SERVLET_NAME, "@@Telephone : " , Telephone, Logger.INFO, Logger.LOG_COMMON);
			if (Telephone.trim().length() > 0 && !Telephone.trim().equals(Constant.STR_ZERO))
			{
				PhoneNumber phoneNumber = new PhoneNumber();
				phoneNumber.setFormattedValue(Telephone);
				entResp.setTelNum(phoneNumber);
			}
			else
				entResp.setTelNum(new PhoneNumber());
			
			if(EntType.equalsIgnoreCase(STANDALONE))
			{
				entResp.setAgency(false);
				entResp.setArCash(false);
				entResp.setApCash(false);
			}
			else if(EntType.equalsIgnoreCase(FULL_AGENCY))
			{
				entResp.setAgency(true);
				entResp.setArCash(true);
				entResp.setApCash(true);
			}
			else if(EntType.equalsIgnoreCase(AGENCY_RECEPTS))
			{
				entResp.setAgency(true);
				entResp.setArCash(true);
				entResp.setApCash(false);
			}
			else if(EntType.equalsIgnoreCase(AGENCY_DISB))
			{
				entResp.setAgency(true);
				entResp.setArCash(false);
				entResp.setApCash(true);
			}
			else if(EntType.equalsIgnoreCase(NO_CASH_AGENCY))
			{
				entResp.setAgency(true);
				entResp.setArCash(false);
				entResp.setApCash(false);
			}
			
			if(SupEntNo.length() > 0)
				entResp.setSEntID(STKGeneral.getInteger(SupEntNo));
			else
				entResp.setSEntID(0);
						
			entResp.setAgentEntID(STKGeneral.getInteger(OwningAgencyNo));
			//entResp.setNextCheckNum(STKGeneral.getInteger(Operating)); removed 12/1/16 CR444
			entResp.setSecCheckNum(STKGeneral.getInteger(Security));
			
			/*removed 12/1/16 CR444
			if(aDBMode.trim().equalsIgnoreCase(Constant.INSERT_MODE))
			{
				entResp.setInterCheckNum(0);
				
			}*/   
			
			GLAccount cashRcptsGL = new GLAccount();
			String cashReceiptsGLMajor = "";
			String cashReceiptsGLSub = "";
			
			Logger.log(new UserRequest(), SERVLET_NAME, "@@CashReceiptsGL : " , CashReceiptsGL, Logger.INFO, Logger.LOG_COMMON);
			
			if(!CashReceiptsGL.equals(""))
			{
				if(CashReceiptsGL.indexOf("-") > -1)
				{
					cashReceiptsGLMajor = CashReceiptsGL.substring(0,CashReceiptsGL.indexOf("-"));
					cashReceiptsGLSub = CashReceiptsGL.substring(CashReceiptsGL.indexOf("-")+1,CashReceiptsGL.length());
				}
				else
				{
					cashReceiptsGLMajor = CashReceiptsGL.substring(0,CashReceiptsGL.length());
					cashReceiptsGLSub = "0";
				}
				cashRcptsGL.setGlMajorAcct(STKGeneral.getInteger(cashReceiptsGLMajor));
				cashRcptsGL.setGlSubAcct(STKGeneral.getInteger(cashReceiptsGLSub));
				entResp.setCashRecptGL(cashRcptsGL);
			}
			else
			{
				entResp.setCashRecptGL(new GLAccount());
			}
			
			/*GLAccount cashDisbGL = new GLAccount();
			String cashDisbGLMajor = "";
			String cashDisbGLSub = "";
			if(!CashDisbGL.equals(""))
			{
				if(CashDisbGL.indexOf("-") > -1)
				{
					cashDisbGLMajor = CashDisbGL.substring(0,CashDisbGL.indexOf("-"));
					cashDisbGLSub = CashDisbGL.substring(CashDisbGL.indexOf("-")+1,CashDisbGL.length());
				}
				else
				{
					cashDisbGLMajor = CashDisbGL.substring(0,CashDisbGL.length());
					cashDisbGLSub = "0";
				}
				cashDisbGL.setGlMajorAcct(STKGeneral.getInteger(cashDisbGLMajor));
				cashDisbGL.setGlSubAcct(STKGeneral.getInteger(cashDisbGLSub));
				entResp.setCashDisbGL(cashDisbGL);
			}
			else
			{
				entResp.setCashDisbGL(new GLAccount());
			}*/
			
			GLAccount entIntercolGL = new GLAccount();
			String entIntercolGLMajor = "";
			String entIntercolGLSub = "";
			if(!EntIntercoGL.equals(""))
			{
				if(EntIntercoGL.indexOf("-") > -1)
				{
					entIntercolGLMajor = EntIntercoGL.substring(0,EntIntercoGL.indexOf("-"));
					entIntercolGLSub = EntIntercoGL.substring(EntIntercoGL.indexOf("-")+1,EntIntercoGL.length());
				}
				else
				{
					entIntercolGLMajor = EntIntercoGL.substring(0,EntIntercoGL.length());
					entIntercolGLSub = "0";
				}
				entIntercolGL.setGlMajorAcct(STKGeneral.getInteger(entIntercolGLMajor));
				entIntercolGL.setGlSubAcct(STKGeneral.getInteger(entIntercolGLSub));
				entResp.setAcctCurrGL(entIntercolGL);
			}
			else
			{
				entResp.setAcctCurrGL(new GLAccount());
			}
			
			GLAccount agencyIntercolGL = new GLAccount();
			String agencyIntercolGLMajor = "";
			String agencyIntercolGLSub = "";
			if(!AgencyIntercoGL.equals(""))
			{	
				if(AgencyIntercoGL.indexOf("-") > -1)
				{
					agencyIntercolGLMajor = AgencyIntercoGL.substring(0,AgencyIntercoGL.indexOf("-"));
					agencyIntercolGLSub = AgencyIntercoGL.substring(AgencyIntercoGL.indexOf("-")+1,AgencyIntercoGL.length());
				}
				else
				{
					agencyIntercolGLMajor = AgencyIntercoGL.substring(0,AgencyIntercoGL.length());
					agencyIntercolGLSub = "0";
				}
				agencyIntercolGL.setGlMajorAcct(STKGeneral.getInteger(agencyIntercolGLMajor));
				agencyIntercolGL.setGlSubAcct(STKGeneral.getInteger(agencyIntercolGLSub));
				entResp.setAgentAcctCurrGL(agencyIntercolGL);
			}
			else
			{
				entResp.setAgentAcctCurrGL(new GLAccount());
			}
			
			GLAccount tenRcblGL = new GLAccount();
			String tenRcblGLMajor = "";
			String tenRcblGLSub = "";
			if(!TenRecvblGL.equals(""))
			{
				if(TenRecvblGL.indexOf("-") > -1)
				{
					tenRcblGLMajor = TenRecvblGL.substring(0,TenRecvblGL.indexOf("-"));
					tenRcblGLSub = TenRecvblGL.substring(TenRecvblGL.indexOf("-")+1,TenRecvblGL.length());
				}
				else
				{
					tenRcblGLMajor = TenRecvblGL.substring(0,TenRecvblGL.length());
					tenRcblGLSub = "0";
				}
				tenRcblGL.setGlMajorAcct(STKGeneral.getInteger(tenRcblGLMajor));
				tenRcblGL.setGlSubAcct(STKGeneral.getInteger(tenRcblGLSub));
				entResp.setTenRecvGL(tenRcblGL);
			}
			else
			{
				entResp.setTenRecvGL(new GLAccount());
			}
			
			GLAccount apGL = new GLAccount();
			String apGLMajor = "";
			String apGLSub = "";
			if(!APGL.equals(""))
			{
				if(APGL.indexOf("-") > -1)
				{
					apGLMajor = APGL.substring(0,APGL.indexOf("-"));
					apGLSub = APGL.substring(APGL.indexOf("-")+1,APGL.length());
				}
				else
				{
					apGLMajor = APGL.substring(0,APGL.length());
					apGLSub = "0";
				}
				apGL.setGlMajorAcct(STKGeneral.getInteger(apGLMajor));
				apGL.setGlSubAcct(STKGeneral.getInteger(apGLSub));
				entResp.setApGL(apGL);
			}
			else
			{
				entResp.setApGL(new GLAccount());
			}
			
			if(DistributionAmt.equals("0.00") || DistributionAmt.equals(""))
				entResp.setDistribAmt(0);
			else
				entResp.setDistribAmt(STKGeneral.getDouble(DistributionAmt));
			
			if(DistributionFreq.length() > 0)
				entResp.setDistribFreq(DistributionFreq.charAt(0));
			else
				entResp.setDistribFreq('M');
			
			entResp.setStartMonth(STKGeneral.getInteger(StartMnth));
			
			if(chkPBIFile.equalsIgnoreCase(Constant.CHAR_YES))
			{
				entResp.setProdInterFile(true);
			}
			else
			{
				entResp.setProdInterFile(false);
			}	
			
			if(chkDeleted.equalsIgnoreCase(Constant.CHAR_YES))
			{
				entResp.setDeleted(true);
			}
			else
			{
				entResp.setDeleted(false);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
		return entResp;
	}	
	
	/**
	 * To get values for payment terms and interest type Calculation combo 
	 * @param type			Code Type
	 * @param appSession	AppSession Object
	 * @return Vector
	 * @throws Exception
	 */
	public Vector getCodeDetails(String type,AppSession appSession) throws Exception
	{
		 String sql = " SELECT CODE_ID, DESCRIPTION "
					+ " FROM CODES_DETAIL "
					+ " WHERE SYS_KEY1 = '" + appSession.getSysKey1() + "' "
					+ " AND CODE_TYPE  = '" + type + "' "
					+ " AND CODE_ID	   > ' ' "
					+ " ORDER BY SORT_SEQ_NO ";
		 Logger.log(new UserRequest(), SERVLET_NAME,"@@getCodeDetails : ",sql,Logger.INFO,Logger.LOG_COMMON);				
		 Vector retVect = DbManager.getQueryResult(new Vector(), sql, appSession.getJDBCPoolName());
		 return retVect ;
	}
	
	/**
	 * This method returns the system parameter value for the given field number.
	 * @param aSysParamColl Hashtable of field collection
	 * @param aFieldNum system parameter field number
	 * @return String system parameter value
	 * @throws Exception
	 */
	private String getSysParmValue(Hashtable aSysParamColl, String aFieldNum) throws Exception
	{
		String sysParmValue = "";
		if (aSysParamColl != null)
		{
			IBSSystemParameter sysParam = (IBSSystemParameter) aSysParamColl.get(aFieldNum);
			if (sysParam != null)
			{
				sysParmValue = sysParam.getParamValue();
			}
		}
		return sysParmValue;
	}
	
	/**
	 * This method is used to get the menu id dynamically
	 * @param appSession	AppSession	Object
	 * @param poolName		Connected DB Pool name 
	 * @param sysKey1		Logged in System Key
	 * @param callPgm		Call Program
	 * @param param1		Param1 field
	 * @return String of Menu Id
	 * @throws Exception
	 */
	public String getMenuId(AppSession appSession,String poolName, String sysKey1, String callPgm, String param1) throws Exception
    {
        Vector retVector = new Vector();
        String retMenuId = "";
        try
		{
	        String sql = " SELECT MENU_ID " 
	        		   + " FROM " + TableConstant.TABLE_MENU  
	                   + " WHERE SYS_KEY1 = '" + sysKey1 + "' "
	                   + " AND CALL_PROGRAM = '" + callPgm + "' " 
	                   + " AND PARAM1 = '" + param1 + "' "; 
	        Logger.log(SERVLET_NAME,"@@getMenuId-sql : ",sql,Logger.INFO,Logger.LOG_COMMON);
	        
	        Logger.log(SERVLET_NAME,"@@getMenuId()","@@sql "+sql,Logger.INFO,Logger.LOG_COMMON);
	        
	        retVector = DbManager.getQueryResult(new Vector(),sql,poolName);
	        Logger.log(SERVLET_NAME,"@@getMenuId()","@@retVector : "+retVector,Logger.INFO,Logger.LOG_COMMON);
	        
	        if(retVector != null && retVector.size() > 0)
	            retMenuId = (String)((Vector)retVector.elementAt(0)).elementAt(0);
		}
        catch(Exception e)
		{
        	e.printStackTrace();
		}	        
        return retMenuId ;
    }
	
	/**
	 * Get the combo data from DB for Property Module
	 * @param appSession	AppSession Object
	 * @param Type			Code Type
	 * @return Vector
	 * @throws Exception
	 */
	private Vector comboVector(AppSession appSession, String Type) throws Exception 
	{
		String sql = "";
		long rowsAffected = 0;
		String returnValue = "";
		Vector resVect = new Vector();
		
		try 
		{
		    sql = " SELECT CODE_ID,DESCRIPTION " + " FROM "
		        + TableConstants.TABLE_CODES_DETAIL + " WHERE SYS_KEY1 = '"
		        + appSession.getSysKey1() + "' " + " AND CODE_TYPE = '" + Type + "'"
		        + " ORDER BY SORT_SEQ_NO ";
		
		    Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@sql : ", "@@Type : "+Type, Logger.INFO, Logger.LOG_COMMON);
		    Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@sql : ","@@sql : "+sql, Logger.INFO, Logger.LOG_COMMON);
		
		    resVect = DbManager.getQueryResult(new Vector(), sql, appSession.getJDBCPoolName());
		    Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@sql : ","@@resVect "+resVect, Logger.INFO, Logger.LOG_COMMON);
		
		}  
		catch (Exception e) 
		{
		    throw e;
		}
		
		return resVect;
	}
	
	/**
	 * This method is used to get the description for Entity / SuperEntity
	 * @param req
	 * @param appSession
	 * @param cntlValue
	 * @param fromSuperEntity
	 * @return String
	 * @throws Exception
	 */
	public String getDesc(HttpServletRequest req,AppSession appSession,String cntlValue,boolean fromSuperEntity)throws Exception
	{
		String result 	 = "";
		String desc 	 = "";
		String fiscalMonth 	 = "";
		String sql 		 = "";
		
		Vector retVector = new Vector();
		
		if(fromSuperEntity)
		{
			 sql = " SELECT SENAME, SESTMO FROM " + TableConstant.TABLE_SENTITY +
			  		" WHERE SYS_KEY1 = '" + appSession.getSysKey1() + "' AND SENDID = " + cntlValue + //;
			  		" AND DELFLG != 'FF' ";
		}
		else 
		{
			  sql = " SELECT ENNAME FROM " + TableConstant.TABLE_ENTITY +
			  		" WHERE SYS_KEY1 = '" + appSession.getSysKey1() + "' AND ENID = " + cntlValue;
		}
		try
		{	
			retVector = DbManager.getQueryResult(new Vector(),sql,appSession.getJDBCPoolName());
			
			if (retVector != null && retVector.size() > 0)
			{
				for(int i=0;i<retVector.size();i++)
				{
					desc = (String)((Vector)retVector.elementAt(i)).elementAt(0);
					if(fromSuperEntity)
					{
					    fiscalMonth = (String)((Vector)retVector.elementAt(i)).elementAt(1);
					}
				}
			}
			Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@getDesc :fromSuperEntity,, "+fromSuperEntity ,"@@desc.."+desc, Logger.FATAL, Logger.LOG_COMMON);
		}	
		catch(Exception ex)
		{
			throw ex;
		}
		if(desc != null && desc.trim().length() > 0)
		{
			if(fromSuperEntity)
			{
				
				result = "<DATA>SUPERENT</DATA>"
					+"<NAME>" + STKGeneral.nullCheck(desc) + "</NAME>"
					+"<FISCALMTH>" + STKGeneral.nullCheck(fiscalMonth) + "</FISCALMTH>";
				
			}
			else
			{
				
				result = "<DATA>ENTIDQUERY</DATA>"
					+"<RESULT>"+Constant.CHAR_YES+"</RESULT>"
					+"<NAME>" + STKGeneral.nullCheck(desc)+ "</NAME>";
			}
		}
		//Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@getDesc : result.." ,result, Logger.FATAL, Logger.LOG_COMMON);
		return result;
	}
	/**
	 * This method is used to get the fiscal start month of the given super entity 
	 * which is used to compare with fiscal st.month while creating new Entity
	 * @param req			HttpServletRequest
	 * @param appSession	AppSession
	 * @param cntlValue		Fiscal Start Month Control value
	 * @return String
	 * @throws Exception
	 */
	public String getFiscalMnthValue(HttpServletRequest req,AppSession appSession,String cntlValue)throws Exception
	{
		String result = "";
		Vector retVector = new Vector();
		String fiscalMonth = "";
		
		Vector tmpResult = new Vector();
		StringBuffer sb = new StringBuffer();
		StringBuffer newStrBuff = new StringBuffer();
		
		String sql = " SELECT SESTMO FROM " + TableConstant.TABLE_SENTITY +
					 " WHERE SYS_KEY1 = '" + appSession.getSysKey1() + "' AND SENDID = " + cntlValue + " ";
		
		Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@getValidValues-sql : " ,sql , Logger.INFO, Logger.LOG_COMMON);
		
		try
		{	
			retVector = DbManager.getQueryResult(new Vector(),sql,appSession.getJDBCPoolName());
			
			if (retVector != null && retVector.size() > 0)
			{
				for(int i=0;i<retVector.size();i++)
				{
					fiscalMonth = (String)((Vector)retVector.elementAt(i)).elementAt(0);
					fiscalMonth = STKGeneral.getPadString(fiscalMonth,"0",2,"L");
					
					sb.append(fiscalMonth);
				}
			}
			
			if(sb.toString().endsWith(","))
			{
				newStrBuff.append(sb.toString().substring(0, sb.length()-1));
			}
			else
			{
				newStrBuff.append(sb);
			}
			Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@getFiscalMnthValue-newStrBuff.tostring : " ,newStrBuff.toString(), Logger.INFO, Logger.LOG_COMMON);
		}	
		catch(Exception ex)
		{
			throw ex;
		}
		
		result = "<DATA>GETVALUE</DATA>"
				+"<VALUE>" + STKGeneral.nullCheck(newStrBuff.toString()) + "</VALUE>";
		
		return result;
	}
	
	/**
	 * Author : E1230
	 * Date	  : 09-Jul-2007
	 * Reason : To check access right Msg for Entity and Property
	 * @param entId			Entity Id
	 * @param propId		Property Id
	 * @param appSession	AppSession Object
	 * @param session		HttpSession Object
	 * @return Vector
	 * @throws Exception
	*/
	private Vector getEntPropAccessRightMsg(String entId,String propId,AppSession appSession,HttpSession session) throws Exception
	{
		Vector resultMsg = new Vector();
		final String USE_PROFILE_VALUE = Constant.CHAR_YES;
		
			if (entId.trim().length()>0 && propId.trim().length()>0)
			{
				entId ="";
			}
			//String useProfileValue = STKGeneral.nullCheck((String)session.getAttribute("USE_PROFILE")).trim();
			
			ValidateAccs  validateAccs = new ValidateAccs(entId,propId,appSession,"Y",USE_PROFILE_VALUE);
			String validMsg = validateAccs.doValidation();
			
			boolean bEntViewAccess = validateAccs.getEntViewAccess();
			boolean bEntUpdateAccess = validateAccs.getEntUpdateAccess();
			boolean bEntDeleteAccess = validateAccs.getEntDeleteAccess();
			
			boolean bPropViewAccess = validateAccs.getPropViewAccess();
			boolean bPropUpdateAccess = validateAccs.getPropUpdateAccess();
			boolean bPropDeleteAccess = validateAccs.getPropDeleteAccess();
		
			resultMsg.add(validMsg);
			
			if (bEntViewAccess)
				resultMsg.add(Constant.CHAR_YES);
			else
				resultMsg.add(Constant.CHAR_NO);
		
			if (bEntUpdateAccess)
				resultMsg.add(Constant.CHAR_YES);
			else
				resultMsg.add(Constant.CHAR_NO);
		
			if (bEntDeleteAccess)
				resultMsg.add(Constant.CHAR_YES);
			else
				resultMsg.add(Constant.CHAR_NO);
	
			if (bPropViewAccess)
				resultMsg.add(Constant.CHAR_YES);
			else
				resultMsg.add(Constant.CHAR_NO);
		
			if (bPropUpdateAccess)
				resultMsg.add(Constant.CHAR_YES);
			else
				resultMsg.add(Constant.CHAR_NO);
		
			if (bPropDeleteAccess)
				resultMsg.add(Constant.CHAR_YES);
			else
				resultMsg.add(Constant.CHAR_NO);
	
			resultMsg.addElement(validateAccs.getPropertyDescription());
			resultMsg.addElement(validateAccs.getEntityDescription());			
			
		return resultMsg;
	}
	
	public String zipCodeValidation(HttpServletRequest req,AppSession appSession)throws Exception
	{
		String result = "";
		Vector retVector = new Vector();		
		
		String zip		= STKGeneral.nullCheck((String) req.getParameter("zip")).trim();
		String state	= STKGeneral.nullCheck((String) req.getParameter("state")).trim();
		String city 	= STKGeneral.nullCheck((String) req.getParameter("city")).trim();
		
		String sql = " SELECT *  FROM  zip_codes " +
					 //" WHERE ZIP = '" + zip + "' AND state_cd = '" + state + "' AND city = '" + city + "' ";
					 " WHERE ZIP = '" + zip + "' AND UPPER(TRIM (state_cd)) = '" + state.toUpperCase() + "' AND UPPER(TRIM (city)) = '" + city.toUpperCase() + "' ";
		
		Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@zipCodeValidation-sql : " ,sql , Logger.FATAL, Logger.LOG_COMMON);
		
		try
		{	
			retVector = DbManager.getQueryResult(new Vector(),sql,appSession.getJDBCPoolName());
			
			if (retVector != null && retVector.size() > 0)
				result = "<DATA>YES</DATA>";
			else
				result = "<DATA>NO</DATA>";
			
			
			Logger.log(appSession.getUserRequest(), SERVLET_NAME, "@@zipCodeValidation-result::" ,result+"##", Logger.FATAL, Logger.LOG_COMMON);
		}	
		catch(Exception ex)
		{
			throw ex;
		}		
		return result;
	}
}