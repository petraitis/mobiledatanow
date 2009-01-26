package com.framedobjects.dashwell.handlers;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.XmlFormatter;

import wsl.fw.exception.MdnException;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.LanguageDobj;

public class FileUploadHandler {
	/**
	 * Check duplicate language name
	 * @param intLanguageId
	 * @param languageName
	 * @return
	 */
	public boolean isDuplicateLanguageName(int intLanguageId, String languageName){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		LanguageDobj langWithSameName;
		try {
			langWithSameName = dataAgent.getLanguageByName(languageName);
			//This is new one
			if (intLanguageId == -1){
				if (langWithSameName != null)	
					return true;				
			}else{//this is existing one
				LanguageDobj oldLang = dataAgent.getLanguageById(intLanguageId);
				if (oldLang != null){
					if (langWithSameName != null){	
						if (!langWithSameName.getName().equalsIgnoreCase(oldLang.getName()))
							return true;			
					}					
				}
			}
			
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}
	/**
	 * check duplicate driver name
	 * @param intDriverId
	 * @param driverName
	 * @return
	 */
	public boolean isDuplicateDriverName(int intDriverId, String driverName){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		JdbcDriver driverWithSameName;
		try {
			driverWithSameName = dataAgent.getJdbcDriverByName(driverName);
			//This is new one
			if (intDriverId == -1){
				if (driverWithSameName != null)	
					return true;				
			}else{//this is existing one
				JdbcDriver oldDriver = dataAgent.getJdbcDriverById(intDriverId);
				if (oldDriver != null){
					if (driverWithSameName != null){	
						if (!driverWithSameName.getName().equals(oldDriver.getName()))
							return true;			
					}					
				}
			}
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}	
}
