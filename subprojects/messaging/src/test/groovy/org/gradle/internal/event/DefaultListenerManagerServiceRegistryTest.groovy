/*
 * Copyright 2021 the original author or authors.
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

package org.gradle.internal.event

import org.gradle.internal.service.DefaultServiceRegistry
import org.gradle.internal.service.scopes.EventScope
import org.gradle.internal.service.scopes.Scopes
import org.gradle.internal.service.scopes.StatefulListener
import spock.lang.Specification

class DefaultListenerManagerServiceRegistryTest extends Specification {
    def listenerManager = new DefaultListenerManager(Scopes.BuildTree)
    def services = new DefaultServiceRegistry()

    def setup() {
        services.add(listenerManager)
    }

    def "automatically creates and registers stateful listeners when first event is broadcast"() {
        def created = Mock(Runnable)
        def listener = Mock(TestListener)

        when:
        services.addProvider(new Object() {
            TestListener createListener() {
                created.run()
                return listener
            }
        })
        def broadcast = listenerManager.getBroadcaster(TestListener)

        then:
        0 * _

        when:
        broadcast.something("12")

        then:
        1 * created.run()
        1 * listener.something("12")
        0 * _
    }

    def "automatically registers stateful listeners when first event is broadcast from child"() {
        expect: false
    }

    def "does not create listeners if no event is broadcast"() {
        expect: false
    }

    def "registers listeners that have already been created prior to first event"() {
        expect: false
    }

    def "registers listeners that are registered before listener manager"() {
        given:
        def listener = Mock(TestListener)
        def services = new DefaultServiceRegistry()
        services.addProvider(new Object() {
            TestListener createTestListener() {
                return listener
            }
        })
        services.addProvider(new Object() {
            DefaultListenerManager createListenerManager() {
                return listenerManager
            }
        })
        def broadcast = services.get(ListenerManager).getBroadcaster(TestListener)

        when:
        broadcast.something("12")

        then:
        1 * listener.something("12")
        0 * listener._
    }

    def "fails when listener manager is not declared as annotation handler"() {
        given:
        def services = new DefaultServiceRegistry()

        when:
        services.add(ListenerManager, new DefaultListenerManager(Scopes.BuildTree))

        then:
        def e = thrown(IllegalStateException)
        e.message == 'Service implements AnnotatedServiceLifecycleHandler but is not declared as a service of this type. This service is declared as having type org.gradle.internal.event.ListenerManager.'
    }

    def "fails when listener manager factory is not declared as annotation handler"() {
        given:
        def services = new DefaultServiceRegistry()
        services.addProvider(new Object() {
            ListenerManager createListenerManager() {
                return new DefaultListenerManager(Scopes.BuildTree)
            }
        })

        when:
        services.get(ListenerManager.class)

        then:
        def e = thrown(IllegalStateException)
        e.message == 'Service implements AnnotatedServiceLifecycleHandler but is not declared as a service of this type. This service is declared as having type org.gradle.internal.event.ListenerManager.'
    }

    def "fails when listener instance is not declared as listener type"() {
        expect: false
    }

    def "fails when listener cannot be created"() {
        expect: false
    }

    @EventScope(Scopes.BuildTree)
    @StatefulListener
    interface TestListener {
        void something(String param)
    }
}
