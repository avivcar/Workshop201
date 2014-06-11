package server.protocol;

import java.util.Vector;

import com.sun.corba.se.impl.activation.CommandHandler;

import server.reactor.ConnectionHandler;
import user.User;
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
		System.out.println("proc message "+msg);
    	String response = null;
    	
    	//breaking msg into array splitting by space
        String[] msgArr = msg.split("\\^");
        for (int i=0;i<msgArr.length;i++)
        	System.out.println("msg "+i+" :"+msgArr[i]);
        if(msgArr[0]!=null){
        	switch(msgArr[0]){
        
				
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
				response = Constants.SIGNUP+"^"+ Constants.ERR_PARAM;
				}
				else response= this.signup(msgArr);
				
				break;
				
			case Constants.GETFORUMS:
			response=Constants.GETFORUMS+"^"+Constants.SUCC_;
				for(int i=0;i<this.forumSystem.forums.size();i++)
					response=response+"^"+this.forumSystem.forums.get(i).getName()+"^"+this.forumSystem.forums.get(i).getId();	
	
				break;
								
				
			case Constants.ISMEMBER:
				if(this.isNull(msgArr,2)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;
			}
			else response =Constants.SUCC_+forumSystem.isMember(msgArr[1], user);
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

			case Constants.CREATE_MESSAGE:
				if(this.isNull(msgArr,5)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else{
					response=Constants.CREATE_MESSAGE+"^"+Constants.SUCC_+"^";
					if (forumSystem.createMessage(msgArr[1], msgArr[2], this.user, msgArr[3], msgArr[4])==null)
						response+=Boolean.toString(false);
					else response+=Boolean.toString(true);
							
				}
				break;
				
			case Constants.AMIADMIN:
				if(this.isNull(msgArr,1)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else 
				response= Constants.SUCC_ + Boolean.toString(forumSystem.isAdmin(msgArr[1],this.user));
			    break;
			    
			case Constants.GETSUBFORUMS:
				if(this.isNull(msgArr,2)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else 
				response=Constants.GETSUBFORUMS+"^"+Constants.SUCC_;
				response+=forumSystem.getSubForums(msgArr[1]);
			    break;
			    
		case Constants.GETMESSAGES:
				if(this.isNull(msgArr,3)){
					print(461, "ERR_PARAMETERS");
					response = Constants.ERR_PARAM;	
				}
				else 
				response=Constants.GETMESSAGES+"^"+Constants.SUCC_;
				response+= this.getSubforumMessages(msgArr);
			    break;
			    
		case Constants.GETREPLIES:
			if(this.isNull(msgArr,4)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else 
			response=Constants.GETREPLIES+"^"+Constants.SUCC_;
			response+= this.getMsgReplies(msgArr);
		    break;
				
		case Constants.ADDREPLY:
			if(this.isNull(msgArr,6)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else 
			response=Constants.ADDREPLY+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.addReply(msgArr[1], msgArr[2], msgArr[3],this.user, msgArr[4], msgArr[5]));
		    break;
				
		case Constants.ADDFORUM:
			if(this.isNull(msgArr,2)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.ADDFORUM+"^"+Constants.SUCC_+"^";
			String newforum = this.forumSystem.createForum(msgArr[1], this.user);
			Forum newf = this.forumSystem.getForum(newforum);
			if(newf==null)response+=Boolean.toString(false);
			else response+=newf.getName()+"^"+newf.getId();
			}
		    break;
		    
		case Constants.ADDSUBFORUM:
			if(this.isNull(msgArr,3)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.ADDSUBFORUM+"^"+Constants.SUCC_+"^";
			response+=addsubforum(msgArr);
			}
		    break;
		    
		case Constants.REMOVEADMIN:
			if(this.isNull(msgArr,3)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.REMOVEADMIN+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.removeAdmin(msgArr[1], this.user, msgArr[2]));
			}
		    break;
			
			
		case Constants.DELETEFORUM:
			if(this.isNull(msgArr,2)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.DELETEFORUM+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.deleteForum(this.user, msgArr[1]));
			}
		    break;
		    
		case Constants.SUBFORUMOPTIONS:
			if(this.isNull(msgArr,3)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.SUBFORUMOPTIONS+"^"+Constants.SUCC_+"^";
			response+=getSubForumOptions(msgArr);
			}
		    break;
				
		case Constants.REMOVEMODERATOR:
			if(this.isNull(msgArr,4)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.REMOVEMODERATOR+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.removeModerator(msgArr[1], msgArr[2], this.user, msgArr[3]));
			}
		    break;
		    
		case Constants.ADDMODERATOR:
			if(this.isNull(msgArr,4)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.ADDMODERATOR+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.addModerator(msgArr[1], msgArr[2], this.user, msgArr[3]));
			}
		    break;				
			    			    				
		case Constants.COMPLAIN:
			if(this.isNull(msgArr,5)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			 response=Constants.COMPLAIN+"^"+Constants.SUCC_+"^";
			 response+=Boolean.toString(forumSystem.createComplaint(msgArr[1], msgArr[2], this.user, msgArr[3], msgArr[4]));
			}
		    break;	
		    
		case Constants.ADDADMIN:
			if(this.isNull(msgArr,3)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			 response=Constants.ADDADMIN+"^"+Constants.SUCC_+"^";
			 response+=Boolean.toString(forumSystem.addAdmin(msgArr[1], this.user, msgArr[2]));
			}
			break;				
			    			    				
		case Constants.FORUMOPTIONS:
			if(this.isNull(msgArr,2)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			 response=Constants.FORUMOPTIONS+"^"+Constants.SUCC_+"^";
			 response+=getForumOptions(msgArr);
			}
		    break;				
			    			    				
		case Constants.ADDFRIEND:
			if(this.isNull(msgArr,3)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			 response=Constants.ADDFRIEND+"^"+Constants.SUCC_+"^";
			 response+=Boolean.toString(forumSystem.friendRequest(msgArr[1], this.user, msgArr[2]));
			}
		    break;	
		    
		case Constants.SETUSERRANK:
			if(this.isNull(msgArr,4)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			 response=Constants.SETUSERRANK+"^"+Constants.SUCC_+"^";
			 response+=Boolean.toString(forumSystem.setRank(msgArr[1], this.user, msgArr[2], msgArr[3]));
			}
		    break;				
			    					    			    				
		case Constants.EDITMESSAGE:
			if(this.isNull(msgArr,4)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.EDITMESSAGE+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.editMessage(this.user, msgArr[1], msgArr[2],msgArr[3]));
			}
		    break;				
			    		
		case Constants.DELETEMESSAGE:
			if(this.isNull(msgArr,2)){
				print(461, "ERR_PARAMETERS");
				response = Constants.ERR_PARAM;	
			}
			else {
			response=Constants.DELETEMESSAGE+"^"+Constants.SUCC_+"^";
			response+=Boolean.toString(forumSystem.deletemessage(msgArr[1], this.user));
			}
		    break;
		    
		case Constants.LOGOUT:
			ConnectionHandler handler=this.user.getConHndlr();
			this.user.addHandler(null);
			this.user=User.buildGuest();
			this.user.addHandler(handler);
			response=Constants.LOGOUT+"^"+Constants.SUCC_+"^"+Boolean.toString(true);
		    break;				

				
			default: response="YAKIR TWAT";
				
        	}
        	              	
        	
        }
        return response;
	}







	private String addsubforum(String[] msgArr) {
		String ans=Boolean.toString(false);
		if(forumSystem.createSubForum(this.user, this.user, msgArr[2], msgArr[1])!=null)
			ans=Boolean.toString(true);
		return ans;
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
    	System.out.println("now in signup:"+ msg.toString());

        user.User user = forumSystem.signup(msg[1],msg[2], msg[3],msg[4], msg[5]);
        if (user!=null) {

        	ans=Constants.SIGNUP+"^"+Constants.SUCC_+"^"+Boolean.toString(true);
        	
        }
        else ans=Constants.SIGNUP+"^"+Constants.SUCC_+"^"+Boolean.toString(false);
    	return ans;
    	
	}
	private String login(String[] msgArr) {
		String ans=null;
		ConnectionHandler newConnection = this.user.getConHndlr();
		this.user.getConHndlr().sayToMe("hi mack-torek");
		
		user.User user = forumSystem.login(msgArr[1], msgArr[2], msgArr[3]);
		if(user!=null){
			ans=Constants.LOGIN+"^"+Constants.SUCC_+"^"+Boolean.toString(true);
			this.user=user;
			this.user.addHandler(newConnection);
			System.out.println("checking updates");
			this.user.checkUpdates();
		}
		else ans=Constants.LOGIN+"^"+Constants.SUCC_+"^"+Boolean.toString(false);
				
	return ans;
	}
	
	private String getSubforumMessages(String[] msgArr) {
		String ans="";
		Forum forum = forumSystem.getForum(msgArr[1]);
		if (forum!=null){
		 SubForum subforum = forum.getSubForumById(msgArr[2]);
		 	if (subforum!=null){
			for(int i=0;i<subforum.getMessages().size();i++)		
				ans+="^"+subforum.getMessages().get(i).getTitle()+"^"+subforum.getMessages().get(i).getId()+"^"
				   +Integer.toString(subforum.getMessages().get(i).getReplies().size())+
				   "^"+subforum.getMessages().get(i).getUser().getUsername();
		 	}
		}	 	
	return ans;
	}
	
	private String getMsgReplies(String[] msgArr) {
		String ans="";
		Message msg = forumSystem.getMessage(msgArr[1], msgArr[2], msgArr[3]);
		if (msg!=null){
			ans+="^"+msg.getTitle()+"^"+msg.getId()+"^"+msg.getContent()+"^"+msg.getUser().getUsername();
			for (int i=0;i<msg.getReplies().size();i++)
				ans+="^"+msg.getReplies().get(i).getTitle()+"^"+msg.getReplies().get(i).getId()
				+"^"+msg.getReplies().get(i).getContent()+"^"+msg.getReplies().get(i).getUser().getUsername();
			
		}

		return ans;
	}
	private String getForumOptions(String[] msgArr) {
		String ans="";
		Forum forum = forumSystem.getForum(msgArr[1]);
		if (forum!=null){
			for (int i=0;i<forum.getMembers().size();i++)
				ans+= forum.getMembers().get(i).getUsername()+"^";
			ans+="***";
			for(int i=0;i<forum.getAdministrators().size();i++)
				ans+="^"+forum.getAdministrators().get(i).getUsername();
			ans+="^***";
			for(int i=0;i<forum.getRanks().size();i++)
				ans+="^"+forum.getRanks().get(i).getName();
		}		
		return ans;
		
		
	}
	
	
	private String getSubForumOptions(String[] msgArr) {
		String ans="";
		Forum forum = forumSystem.getForum(msgArr[1]);
		if (forum!=null){
			for (int i=0;i<forum.getMembers().size();i++)
				ans+= forum.getMembers().get(i).getUsername()+"^";
			ans+="***";
			SubForum subforum =forum.getSubForumById(msgArr[2]);
			if(subforum!=null)
			  for(int i=0;i<subforum.getModerators().size();i++)
			    	ans+="^"+subforum.getModerators().get(i).getUsername();	
		}		
		return ans;
	}
		
}

