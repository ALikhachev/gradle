/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.fixtures

import groovy.transform.SelfType
import org.gradle.test.fixtures.file.TestFile
import org.intellij.lang.annotations.Language

@SelfType(AbstractIntegrationSpec)
trait LanguageSpecificTestFileFixture {

    /**
     * <b>Appends</b> provided code to the {@link #getBuildFile() default build file}.
     * <p>
     * Use {@link #buildScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void buildFile(@GroovyBuildScriptLanguage String append) {
        buildFile << append
    }

    /**
     * <b>Appends</b> provided code to the given build file.
     * <p>
     * Use {@link #buildScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void buildFile(String buildFile, @GroovyBuildScriptLanguage String append) {
        file(buildFile) << append
    }

    /**
     * <b>Appends</b> provided code to the given build file.
     * <p>
     * Use {@link #buildScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void buildFile(TestFile buildFile, @GroovyBuildScriptLanguage String append) {
        buildFile << append
    }

    /**
     * <b>Appends</b> provided code to the {@link #getSettingsFile() default settings file}.
     * <p>
     * Use {@link #settingsScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void settingsFile(@GroovySettingsScriptLanguage String append) {
        settingsFile << append
    }

    /**
     * <b>Appends</b> provided code to the given settings file.
     * <p>
     * Use {@link #settingsScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void settingsFile(String settingsFile, @GroovySettingsScriptLanguage String append) {
        file(settingsFile) << append
    }

    /**
     * <b>Appends</b> provided code to the given settings file.
     * <p>
     * Use {@link #settingsScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void settingsFile(TestFile settingsFile, @GroovySettingsScriptLanguage String append) {
        settingsFile << append
    }

    /**
     * <b>Appends</b> provided code to the {@link #getInitScriptFile() default init script file}.
     * <p>
     * Use {@link #initScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void initScriptFile(@GroovyInitScriptLanguage String append) {
        initScriptFile << append
    }

    /**
     * <b>Appends</b> provided code to the given init script file.
     * <p>
     * Use {@link #initScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void initScriptFile(String initScriptFile, @GroovyInitScriptLanguage String append) {
        file(initScriptFile) << append
    }

    /**
     * <b>Appends</b> provided code to the given init script file.
     * <p>
     * Use {@link #initScript(java.lang.String)} to <b>set (replace)</b> the entire file contents instead.
     */
    void initScriptFile(TestFile initScriptFile, @GroovyInitScriptLanguage String append) {
        initScriptFile << append
    }

    /**
     * <b>Appends</b> provided code to the given Java file.
     */
    void javaFile(String targetFile, @Language('java') String append) {
        file(targetFile) << append
    }

    /**
     * <b>Appends</b> provided code to the given Java file.
     */
    void javaFile(TestFile targetBuildFile, @Language('java') String append) {
        targetBuildFile << append
    }

    /**
     * <b>Appends</b> provided code to the given Groovy file.
     * <p>
     * Consider specialized methods for Gradle scripts:
     * <ul>
     * <li>{@link #buildFile(java.lang.String, java.lang.String)}
     * <li>{@link #settingsFile(java.lang.String, java.lang.String)}
     * <li>{@link #initScriptFile(java.lang.String, java.lang.String)}
     * </ul>
     */
    void groovyFile(String targetFile, @Language('groovy') String append) {
        file(targetFile) << append
    }

    /**
     * <b>Appends</b> provided code to the given Groovy file.
     * <p>
     * Consider specialized methods for Gradle scripts:
     * <ul>
     * <li>{@link #buildFile(org.gradle.test.fixtures.file.TestFile, java.lang.String)}
     * <li>{@link #settingsFile(org.gradle.test.fixtures.file.TestFile, java.lang.String)}
     * <li>{@link #initScriptFile(org.gradle.test.fixtures.file.TestFile, java.lang.String)}
     * </ul>
     */
    void groovyFile(TestFile targetFile, @Language('groovy') String append) {
        targetFile << append
    }

    /**
     * Provides syntax highlighting for the snippet of the build script code.
     *
     * @return the same snippet
     */
    static String buildScriptSnippet(@GroovyBuildScriptLanguage String snippet) {
        snippet
    }

    /**
     * Provides syntax highlighting for the snippet of the settings script code.
     *
     * @return the same snippet
     */
    static String settingsScriptSnippet(@GroovySettingsScriptLanguage String snippet) {
        snippet
    }

    /**
     * Provides syntax highlighting for the snippet of the init script code.
     *
     * @return the same snippet
     */
    static String initScriptSnippet(@GroovyInitScriptLanguage String snippet) {
        snippet
    }

    /**
     * Sets (replacing) the contents of the build.gradle file.
     * <p>
     * To append, use {@link #buildFile(java.lang.String)}
     */
    TestFile buildScript(@GroovyBuildScriptLanguage String script) {
        buildFile.text = script
        buildFile
    }

    /**
     * Sets (replacing) the contents of the settings.gradle file.
     * <p>
     * To append, use {@link #settingsFile(java.lang.String)}
     */
    TestFile settingsScript(@GroovyBuildScriptLanguage String script) {
        settingsFile.text = script
        settingsFile
    }

    /**
     * Sets (replacing) the contents of the settings.gradle file.
     * <p>
     * To append, use {@link #initScriptFile(java.lang.String)}
     */
    TestFile initScript(@GroovyBuildScriptLanguage String script) {
        initScriptFile.text = script
        initScriptFile
    }

}
