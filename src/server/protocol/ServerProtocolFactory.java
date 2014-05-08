package server.protocol;
public interface ServerProtocolFactory {
   AsyncServerProtocol create(user.User user);
}
