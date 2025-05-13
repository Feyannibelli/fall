package edu.austral.dissis.todos.app;

public class Constants {
  public static final String TASK_NAME = "Test task";
  public static final String TASK_DESCRIPTION = "Test description";
  public static final String NEW_TASK_REQUEST =
      """
            {
                "title": "%s",
                "description": "%s",
                "done": false
            }
            """
          .formatted(TASK_NAME, TASK_DESCRIPTION);
  public static final String NEW_TASK_NAME = "New task";
  public static final String NEW_TASK_DESCRIPTION = "New description";
  public static final String UPDATE_TASK_REQUEST =
      """
            {
                "title": "%s",
                "description": "%s",
                "done": true
            }
            """
          .formatted(NEW_TASK_NAME, NEW_TASK_DESCRIPTION);
}
