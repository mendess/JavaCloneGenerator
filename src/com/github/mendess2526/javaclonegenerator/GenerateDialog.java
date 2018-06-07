package com.github.mendess2526.javaclonegenerator;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GenerateDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> myComponent;
    private CollectionListModel<PsiField> myFields;

    protected GenerateDialog(PsiClass psiClass){
        super(psiClass.getProject());
        setTitle("Select Fields to Clone");

        myFields = new CollectionListModel<>(psiClass.getFields());
        JList fieldJList = new JList(myFields);
        fieldJList.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldJList);
        decorator.disableAddAction();
        JPanel panel = decorator.createPanel();
        myComponent = LabeledComponent.create(panel, "Field to include in clone");
        init();

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel(){
        return myComponent;
    }

    public List<PsiField> getMyFields(){
        return myFields.getItems();
    }
}
