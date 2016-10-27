/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.projectresult;

import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.DependencyGraphNode;
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.DependencyGraphVisitor;
import org.gradle.internal.component.local.model.LocalConfigurationMetadata;
import org.gradle.internal.component.model.ConfigurationMetadata;

public class ResolvedLocalComponentsResultGraphVisitor implements DependencyGraphVisitor {
    private final ResolvedLocalComponentsResultBuilder builder;
    private ComponentIdentifier rootId;
    private DependencyGraphNode root;

    public ResolvedLocalComponentsResultGraphVisitor(ResolvedLocalComponentsResultBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void start(DependencyGraphNode root) {
        this.root = root;
        this.rootId = root.getOwner().getComponentId();
    }

    @Override
    public void visitNode(DependencyGraphNode resolvedConfiguration) {
        ComponentIdentifier componentId = resolvedConfiguration.getOwner().getComponentId();
        if (!rootId.equals(componentId) && componentId instanceof ProjectComponentIdentifier) {
            builder.projectConfigurationResolved((ProjectComponentIdentifier) componentId, resolvedConfiguration.getNodeId().getConfiguration());
        }
        ConfigurationMetadata configurationMetadata = resolvedConfiguration.getMetadata();
        if (resolvedConfiguration != root && configurationMetadata instanceof LocalConfigurationMetadata) {
            builder.localComponentResolved(componentId, ((LocalConfigurationMetadata) configurationMetadata).getDirectBuildDependencies());
        }
    }

    @Override
    public void visitEdge(DependencyGraphNode resolvedConfiguration) {
    }

    @Override
    public void finish(DependencyGraphNode root) {
    }
}
