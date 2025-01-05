package lms.pixel.backend.security;

import org.springframework.stereotype.Component;
import lms.pixel.backend.security.permissionCommand.ICommand;
import lms.pixel.backend.utils.AuthHandler;

@Component
public class PermissionChecker {
    private final CommandRegistry commandRegistry;
    private final AuthHandler authService;

    public PermissionChecker(CommandRegistry commandRegistry, AuthHandler authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    public int getUseridByToken(String token) {
        return authService.getUseridByToken(token);
    }

    public boolean checkPermission(String action, int userId, int resourceId) {
        ICommand command = commandRegistry.getCommand(action);
        if (command == null) {
            throw new IllegalArgumentException("No command found for action: " + action);
        }
        return command.execute(userId, resourceId);
    }
}
