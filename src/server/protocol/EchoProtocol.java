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
	//	System.out.println("proc message "+msg);
    	String response = null;
    	
    	//breaking msg into array splitting by space
        String[] msgArr = msg.split(" ");
        System.out.println("splitter: "+msgArr[0]+" "+msgArr[1] );
        if(msgArr[0]!=null){
        	switch(msgArr[0]){
        	
        	case Constants.CREATE_FORUM:
        		if(!this.isNull(msgArr, 2)){
        			response=forumSystem.createForum(msgArr[1],this.user);
        			if (response!=null) response="SUCC_"+response;
        			else response=Constants.ERR_+"CANNOT_CREATE";
        		}
        		
        		else {response =Constants.ERR_PARAM;
        				print(431, "ERR_PARAMETERS");
        			}
				break;
				
				
			case Constants.ISADMIN:
				if(this.isNull(msgArr,2)) {
				response = Constants.ERR_PARAM;
    			print(431, "ERR_PARAMETERS");
				}
				else response= "SUCC_"+Boolean.valueOf(forumSystem.isAdmin(msgArr[1], user));
				break;



			case Constants.SIGNUP:
				if(this.isNull(msgArr, 5)) {
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;
				}
				else response= this.signup(msgArr);
				break;
				
				
			case Constants.ISMEMBER:
				if(this.isNull(msgArr,2)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;
			}
			else if(forumSystem.isMember(msgArr[1], user)) response="SUCC_TRUE";
					else response = "SUCC_FALSE"; 
				break;
				
			case Constants.LOGIN:
				if(this.isNull(msgArr,3)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;
					
				}
				else response =this.login(msgArr);
				
				break;
				
			case Constants.EXISTSUBFORUM:
				if(this.isNull(msgArr, 3)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else 
					response = Constants.SUCC_+Boolean.valueOf(forumSystem.existSubForum(msgArr[1], msgArr[2]));
				
				break;
		/**	case Constants.GET_FORUM:
				if(this.checkNull(msgArr, 3)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else {
					response=forumSystem.getForum(msgArr[1]);
					if (response!=null) response=Constants.SUCC_+response;
					else response = Constants.ERR_+"NO_SUCH_FORUM";
				
				}
			**/
			case Constants.CREATE_MESSAGE:
				if(this.isNull(msgArr,5)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else{ response = forumSystem.createMessage(msgArr[1], msgArr[2],this.user,msgArr[3], msgArr[4]);
						if (response!=null) response=Constants.SUCC_+response;
						else response=Constants.ERR_+"CANNOT";
					
				}
				
			    break;
				
			    
				
				
			default: response="DEFAULT";
				
        	}
        	              	
        	
        }
        return response;
	}




	public boolean isEnd(String msg)
    {
    	
        return msg.equals("QUIT");
    }
    
 
    private boolean isNull(String[] msg, int input){
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
