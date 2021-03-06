// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.lang

import com.intellij.ide.plugins.loadExtensionWithText
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.use
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.LanguageSubstitutor
import com.intellij.psi.PsiManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.util.SystemProperties
import org.assertj.core.api.Assertions.assertThat

class LanguageSubstitutorLoadUnloadTest : LightJavaCodeInsightFixtureTestCase() {
  fun testBefore() {
    if (!SystemProperties.getBooleanProperty("LanguageSubstitutorLoadUnloadTest", false)) {
      return
    }

    myFixture.configureByText("dummy.txt", "package hello;")
  }

  fun testLoadUnload() {
    if (SystemProperties.getBooleanProperty("skip.LanguageSubstitutorLoadUnloadTest", true)) {
      println("Skip LanguageSubstitutorLoadUnloadTest (set VM property skip.LanguageSubstitutorLoadUnloadTest to false to not ignore)")
      return
    }

    val beforeLoading = myFixture.configureByText("dummy.txt", "package hello;")
    assertThat(beforeLoading.language).isInstanceOf(PlainTextLanguage::class.java)

    val virtualFile = beforeLoading.virtualFile
    val text = "<lang.substitutor language=\"TEXT\" implementationClass=\"${TextToJavaSubstitutor::class.java.name}\"/>"
    loadExtensionWithText(text, javaClass.classLoader).use {
      val afterLoading = PsiManager.getInstance(myFixture.project).findFile(virtualFile)
      assertThat(afterLoading!!.language).isInstanceOf(JavaLanguage::class.java)
    }
    val afterUnloading = psiManager.findFile(virtualFile)
    assertThat(afterUnloading!!.language).isInstanceOf(PlainTextLanguage::class.java)
  }
}

private class TextToJavaSubstitutor : LanguageSubstitutor() {
  override fun getLanguage(file: VirtualFile, project: Project): Language? {
    return if (file.name.startsWith("dummy")) JavaLanguage.INSTANCE else null
  }
}