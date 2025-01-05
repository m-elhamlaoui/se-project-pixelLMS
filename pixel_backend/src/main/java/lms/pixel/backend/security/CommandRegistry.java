package lms.pixel.backend.security;

import org.springframework.stereotype.Component;

import lms.pixel.backend.security.permissionCommand.CanAlterCourse;
import lms.pixel.backend.security.permissionCommand.CanAlterTask;
import lms.pixel.backend.security.permissionCommand.CanCreateCourse;
import lms.pixel.backend.security.permissionCommand.CanCreateUsers;
import lms.pixel.backend.security.permissionCommand.CanPromoteUsers;
import lms.pixel.backend.security.permissionCommand.ICommand;
import lms.pixel.backend.security.permissionCommand.IsParticipant;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommandRegistry {

    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandRegistry(CanAlterTask canAlterTask,
                         CanAlterCourse canAlterCourse,
                         IsParticipant isParticipant,
                         CanCreateUsers canCreateUsers,
                         CanCreateCourse canCreateCourse, 
                         CanPromoteUsers canPromoteUsers) 
    {
        registerCommand("canAlterTask", canAlterTask);
        registerCommand("canAlterCourse", canAlterCourse);
        registerCommand("isParticipant", isParticipant);
        registerCommand("canCreateUsers", canCreateUsers);
        registerCommand("canCreateCourse", canCreateCourse);
        registerCommand("canPromoteUsers", canPromoteUsers);
    }


    public void registerCommand(String action, ICommand command) {
        if (commands.containsKey(action)) {
            throw new IllegalArgumentException("Command for action " + action + " is already registered.");
        }
        commands.put(action, command);
    }

    public ICommand getCommand(String action) {
        return commands.get(action);
    }

    public boolean hasCommand(String action) {
        return commands.containsKey(action);
    }
}
