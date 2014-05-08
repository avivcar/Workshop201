package server.protocol;


import java.util.Vector;

import forumSystemCore.*;

/**
 * a simple implementation of the server protocol interface
 */
public class EchoProtocol implements AsyncServerProtocol {

	private boolean _shouldClose = false;
	private boolean _connectionTerminated = false;
	private user.User user;
	private ForumSystem forumSystem;

	public EchoProtocol(user.User user, ForumSystem forumSystem){
		this.user =user;
		this.forumSystem = forumSystem;
		
	}
	@Override
	public String processMessage(String msg) {        
        String tmp = null;
    	String response = null;
    	//breaking msg into array splitting by space
        String[] msgArr = msg.split(" ");
        
        if(msgArr[0]!=null){
        	switch(msgArr[0]){
        	
        	case "CREATE_FORUM":
        		if(!this.checkNull(msgArr, 2)){
        			response=forumSystem.createForum(msgArr[1],this.user);
        			if (response!=null) response="SUCC_"+response;
        			else response="ERR_cant_create_forum";
        		}
        		
        		else {response = "ERR_not_enputh_parameters";
        				print(431, "ERR_PARAMETERS");
        			}
				break;
				
				
			case "ISADMIN":
				if(this.checkNull(msgArr,2)) {
				response = "ERR_not_enputh_parameters";
    			print(431, "ERR_PARAMETERS");
				}
				else response= "SUCC_"+Boolean.valueOf(forumSystem.isAdmin(msgArr[1], user));
				break;



			case "SIGNUP":
				if(this.checkNull(msgArr, 5)) {
				print(461, "ERR_PARAMETERS");
				response = "ERR_not_enputh_parameters";
				}
				else response= this.signup(msgArr);
				break;
				
				
			case "ISMEMBER":
				if(this.checkNull(msgArr,2)){
					print(461, "ERR_PARAMETERS");
					response = "ERR_not_enputh_parameters";
			}
			else if(forumSystem.isMember(msgArr[1], user)) response="SUCC_TRUE";
					else response = "SUCC_FALSE"; 
				break;
				
			case "LOGIN":
				if(this.checkNull(msgArr,3)){
					print(461, "ERR_PARAMETERS");
					response = "ERR_not_enputh_parameters";
					
				}
				else response =this.login(msgArr);
				
				break;
				
			case "":

				
			default: if (true){
				response=null;
		        	
			}
				
        	}
        	              	
        	
        }
        return response;
	}




	public boolean isEnd(String msg)
    {
    	
        return msg.equals("QUIT");
    }
    
 
    private boolean checkNull(String[] msg, int input){
    	return msg.length<input;
    }
     
    public static void print(int error, String msg){
    	System.out.println("ERROR \""+error+"\" : "+msg);
    }
    
	public void connectionTerminated() {
		this._connectionTerminated = true;
	}
	
	public boolean shouldClose() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//new methods!!
	private String signup(String[] msg) {
    	String ans =null;
        user.User user = forumSystem.signup(msg[1], msg[2], msg[3],msg[4], msg[5]);
        if (user!=null) {
        	this.user=user;
        	ans="SUCC_SIGNUP";
        }
        else ans="ERR_SIGNUP";
    	return ans;
    	
	}
	private String login(String[] msgArr) {
		String ans=null;
		user.User user = forumSystem.login(msgArr[1], msgArr[2], msgArr[3]);
		if(user!=null){
			ans="SUCC_"+user.getUsername();
			this.user=user;
		}
		else ans="ERR_LOGIN";
						
					
	return ans;
	}
	
	
	
	
	
}
