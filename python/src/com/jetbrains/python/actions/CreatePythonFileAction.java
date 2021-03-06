// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jetbrains.python.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.jetbrains.python.PyBundle;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.pyi.PyiFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class CreatePythonFileAction extends CreateFileFromTemplateAction implements DumbAware {
  public CreatePythonFileAction() {
    super(PyBundle.message("action.create.python.file.title"), PyBundle.message("action.create.python.file.description"), PythonFileType.INSTANCE.getIcon());
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder
      .setTitle("New Python file")
      .addKind("Python file", PythonFileType.INSTANCE.getIcon(), "Python Script")
      .addKind("Python unit test", PythonFileType.INSTANCE.getIcon(), "Python Unit Test")
      .addKind("Python stub", PyiFileType.INSTANCE.getIcon(), "Python Stub");
  }

  @Override
  protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
    return "Create Python script " + newName; 
  }
}
