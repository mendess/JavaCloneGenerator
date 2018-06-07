package com.github.mendess2526.javaclonegenerator;

import b.g.N;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;
import java.util.Objects;

public class GenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e){
        PsiClass psiClass = getPsiClassFromContext(e);
        if(psiClass == null) return;
        GenerateDialog generateDialog = new GenerateDialog(psiClass);
        generateDialog.show();
        if(generateDialog.isOK()){
            generateCloneConstructor(psiClass, generateDialog.getMyFields());
        }

    }

    private void generateCloneConstructor(PsiClass psiClass, List<PsiField> myFields){
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()){
            @Override
            protected void run() throws Throwable{
                String className = psiClass.getName();
                if(className == null) return;
                String classname = className.substring(0,1).toLowerCase() + className.substring(1);
                StringBuilder s = new StringBuilder("public ");
                s.append(className).append("(").append(className).append(" ")
                        .append(classname).append("){");
                try{
                    if(!"Object".equals(psiClass.getSuperClass().getName()))
                        s.append("\tsuper(").append(classname).append(");");
                }catch(NullPointerException ignored){}
                for(PsiField psiField: myFields){
                    String fieldName = psiField.getName();
                    if(fieldName == null) continue;
                    String getterName = ".get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1)
                                        +"();\n";
                    s.append("\tthis.").append(fieldName).append(" = ")
                            .append(classname)
                            .append(getterName);
                }
                s.append("}");
                PsiMethod cloneConst = JavaPsiFacade.getElementFactory(getProject()).createMethodFromText(s.toString(), psiClass);
                psiClass.add(cloneConst);

            }
        }.execute();
    }

    @Override
    public void update(AnActionEvent e){
        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(true);

    }

    private PsiClass getPsiClassFromContext(AnActionEvent e){
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if(psiFile == null || editor == null){
            e.getPresentation().setEnabled(false);
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if(psiClass == null){
            e.getPresentation().setEnabled(false);
            return null;
        }
        return psiClass;
    }
}
