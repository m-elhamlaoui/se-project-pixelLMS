package lms.pixel.backend.security.permissionCommand;

public interface ICommand {
    boolean execute(int performerId, int resourceId);
}