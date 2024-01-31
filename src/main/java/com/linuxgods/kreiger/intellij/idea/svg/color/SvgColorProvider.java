package com.linuxgods.kreiger.intellij.idea.svg.color;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.ElementColorProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.images.fileTypes.impl.SvgLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

public class SvgColorProvider implements ElementColorProvider {
    @Override
    public @Nullable Color getColorFrom(@NotNull PsiElement element) {
        if (element.getNode().getElementType() != XML_ATTRIBUTE_VALUE_TOKEN) return null;
        if (!(element.getParent() instanceof XmlAttributeValue xmlAttributeValue)) return null;
        if (xmlAttributeValue.getLanguage() != SvgLanguage.INSTANCE) return null;
        String value = xmlAttributeValue.getValue();
        if (value.startsWith("#")) {
            try {
                return Color.decode(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void setColorTo(@NotNull PsiElement element, @NotNull Color color) {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue() );
        if (element.getNode().getElementType() != XML_ATTRIBUTE_VALUE_TOKEN) return;
        if (!(element.getParent() instanceof XmlAttributeValue xmlAttributeValue)) return;
        if (xmlAttributeValue.getLanguage() != SvgLanguage.INSTANCE) return;
        if (! (xmlAttributeValue.getParent() instanceof XmlAttribute xmlAttribute)) return;
        XmlElementFactory xmlElementFactory = XmlElementFactory.getInstance(element.getProject());
        XmlAttribute newAttribute = xmlElementFactory.createAttribute(xmlAttribute.getName(), hex,
                xmlAttribute);
        XmlAttributeValue newValue = newAttribute.getValueElement();
        if (newValue == null) return;
        ASTNode newToken = newValue.getNode().findChildByType(XML_ATTRIBUTE_VALUE_TOKEN);
        ASTNode valueNode = xmlAttributeValue.getNode();
        ASTNode oldToken = valueNode.findChildByType(XML_ATTRIBUTE_VALUE_TOKEN);
        if (oldToken == null || newToken == null) return;
        valueNode.replaceChild(oldToken, newToken);
    }
}
