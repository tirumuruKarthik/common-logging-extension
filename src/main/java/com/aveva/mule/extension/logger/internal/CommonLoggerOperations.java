package com.aveva.mule.extension.logger.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;



/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class CommonLoggerOperations {

	protected transient Log logger = LogFactory.getLog(CommonLoggerOperations.class);
	
	//logger attributes to create the log line
	private String appName;
	private String flowName;
	private String logMessage;
	private String messageId;
	private String messageCorrelationId;
	private String requestId;
	//private String timestamp;
	

	private String additionalInfo;

	public enum LoggerLevelOptions {
		INFO,
		DEBUG
	}	
	


	
	
	/**
	 * Example of an operation that uses the configuration and a connection instance to perform some action.
	 */
	@MediaType(value = ANY, strict = false)
	public String retrieveInfo(@Config CommonLoggerConfiguration configuration, @Connection CommonLoggerConnection connection){
		return "Using Configuration [" + configuration.getConfigId() + "] with Connection id [" + connection.getId() + "]";
	}
	
	@MediaType(value = ANY, strict = false)
	public void callEntryLogger(@Optional String AppName,
			@Optional String FlowName,
			@Optional String TimeStamp,
			@Optional String LogPayload,
			@Optional String MessageID,
			@Optional String messageCorrelationID,
			@Optional String RequestID,
			@Optional String AdditionalInfo) {
		
		logger.info(TimeStamp+ " CALL-ENTRY >> "
				+ buidLogLine(AppName, FlowName, LogPayload,
						MessageID, messageCorrelationID, RequestID, AdditionalInfo));
		
	}

	@MediaType(value = ANY, strict = false)
	public void callExitLogger(@Optional String AppName,
			@Optional String FlowName,
			@Optional String TimeStamp,
			@Optional String LogPayload,
			@Optional String MessageID,
			@Optional String messageCorrelationID,
			@Optional String RequestID,
			@Optional String AdditionalInfo) {
		
		logger.info(TimeStamp+ " CALL-EXIT << "
				+ buidLogLine(AppName, FlowName, LogPayload,
						MessageID, messageCorrelationID, RequestID, AdditionalInfo ));
		
	}
	


		
		@MediaType(value = ANY, strict = false)
		public void processEntryLogger(@Optional String AppName,
				@Optional String FlowName,
				@Optional String TimeStamp,
				@Optional String LogPayload,
				@Optional String MessageID,
				@Optional String messageCorrelationID,
				@Optional String RequestID,
				@Optional String AdditionalInfo) {
		
			logger.info(TimeStamp+ 
					" PROCESSSING <> " + buidLogLine(AppName, FlowName, LogPayload,
							MessageID, messageCorrelationID, RequestID, AdditionalInfo));
			
		}
		
		
		@MediaType(value = ANY, strict = false)
		public void processExitLogger(@Optional String AppName,
				@Optional String FlowName,
				@Optional String TimeStamp,
				@Optional String LogPayload,
				@Optional String MessageID,
				@Optional String messageCorrelationID,
				@Optional String RequestID,
				@Optional String AdditionalInfo) {
	
			logger.info(TimeStamp + "PROCESS-EXIT << " + buidLogLine(AppName, FlowName, LogPayload,
					MessageID, messageCorrelationID, RequestID, AdditionalInfo));
			
		}
		
		/*		  @MediaType(value = ANY, strict = false)
		  public void entryLogger(@Optional String AppName,
					@Optional String FlowName,
					@Optional String TimeStamp,
					@Optional String LogPayload,
					@Optional String MessageID,
					@Optional String MessageCorrelationID,
					@Optional String RequestID,
					@Optional String AdditionalInfo) {
			
					logger.info(TimeStamp+ 
							" ENTRY >> " + buidLogLine(AppName, FlowName, LogPayload, MessageID, MessageCorrelationID, RequestID, AdditionalInfo));
		  }
	  
		@MediaType(value = ANY, strict = false)
		public void exitLogger(@Optional String AppName,
				@Optional String FlowName,
				@Optional String TimeStamp,
				@Optional String LogPayload,
				@Optional String MessageID,
				@Optional String messageCorrelationID,
				@Optional String RequestID,
				@Optional String AdditionalInfo) {
			
			logger.info(TimeStamp+ " EXIT << " + buidLogLine(AppName, FlowName, LogPayload,
					MessageID, messageCorrelationID, RequestID, AdditionalInfo));
			
		}*/
			

		@MediaType(value = ANY, strict = false)
		public void processByLoggerLevel(@Optional String AppName,
				@Optional String FlowName,
				@Optional String TimeStamp,
				@Optional String LogPayload,
				@Optional String MessageID,
				@Optional String messageCorrelationID,
				@Optional String RequestID,
				@Optional String AdditionalInfo,
				@Optional LoggerLevelOptions logLevel) {
	
			
			if (logLevel == null) {
				logLevel = LoggerLevelOptions.INFO;
			}else if( logLevel != LoggerLevelOptions.INFO && logLevel != LoggerLevelOptions.DEBUG )
				logLevel = LoggerLevelOptions.INFO;
	
			
			if (logLevel.toString().equals("DEBUG")){
				logger.debug(TimeStamp+ " PROCESSING <> "
						+ buidLogLine(AppName, FlowName, LogPayload,
								MessageID, messageCorrelationID, RequestID, AdditionalInfo ));
			}
			else
				logger.info(TimeStamp+ " PROCESSING <> "
						+ buidLogLine(AppName, FlowName, LogPayload,
								MessageID, messageCorrelationID, RequestID, AdditionalInfo));
			
		}
	
		@MediaType(value = ANY, strict = false)
		public void warnLogger(@Optional String AppName,
				@Optional String FlowName,
				@Optional String TimeStamp,
				@Optional String LogPayload,
				@Optional String MessageID,
				@Optional String messageCorrelationID,
				@Optional String RequestID,
				@Optional String AdditionalInfo) {
	
			logger.warn(TimeStamp+ " WARN <> " + buidLogLine(AppName, FlowName, LogPayload,
					MessageID, messageCorrelationID, RequestID, AdditionalInfo));
			
		}
	
		@MediaType(value = ANY, strict = false)
		public void errorLogger(@Optional String AppName,
				@Optional String FlowName,
				@Optional String TimeStamp,
				@Optional String LogPayload,
				@Optional String MessageID,
				@Optional String messageCorrelationID,
				@Optional String RequestID,
				@Optional String AdditionalInfo) {
	
			logger.error( TimeStamp+ " ERROR :: " + buidLogLine(AppName, FlowName, LogPayload,
					MessageID, messageCorrelationID, RequestID, AdditionalInfo));
			
		}
	  /**
	   * Private Methods are not exposed as operations
	   */
	  
	  private String buidLogLine(String appName, String flowName, String vLogMessage, String messageID,
			  String messageCorrelationID, String requestID, String addnInfo) {
	
			setAttributes(appName, flowName, vLogMessage, messageID, messageCorrelationID, requestID,  addnInfo);
			
			StringBuilder sb = new StringBuilder();
			sb.append(this.appName != null? "App Name: "+this.appName+"; ":"");
			sb.append(this.flowName != null? "Flow Name: "+this.flowName+"; ":"");
			sb.append(this.requestId != null? "Request ID: "+this.requestId+"; ":"");
			sb.append(this.messageId != null? "Message ID: "+this.messageId+"; ":"");
			sb.append(this.messageCorrelationId != null? "Message Correlation ID: "+this.messageCorrelationId+"; ":"");
			sb.append(this.logMessage != null? "Log Payload: "+this.logMessage+"; ":"");
			sb.append(this.additionalInfo != null? "Additional Info: "+this.additionalInfo:"");
			
			return sb.toString();

		}

	  
	  private void setAttributes(String appName, String flowName, String logMessage, String messageId,
				String messageCorrelationId, String requestId, String addnInfo) {
			this.appName = appName;
			this.flowName = flowName;
			this.logMessage = logMessage;
			this.messageId = messageId;
			this.requestId = requestId;
			this.messageCorrelationId = messageCorrelationId;
			this.additionalInfo = addnInfo;
	  }
	  

	  


}
