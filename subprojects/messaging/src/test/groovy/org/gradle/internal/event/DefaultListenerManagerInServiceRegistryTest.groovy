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

class DefaultListenerManagerInServiceRegistryTest extends Specification {
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
        def created = Mock(Runnable)
        def listener = Mock(TestListener)

        when:
        services.addProvider(new Object() {
            TestListener createListener() {
                created.run()
                return listener
            }
        })
        def broadcast = listenerManager.createChild(Scopes.BuildTree).getBroadcaster(TestListener)

        then:
        0 * _

        when:
        broadcast.something("12")

        then:
        1 * created.run()
        1 * listener.something("12")
        0 * _
    }

    def "registers listeners that have already been created prior to first event"() {
        def listener = Mock(TestListener)

        when:
        services.addProvider(new Object() {
            TestListener createListener() {
                return listener
            }
        })
        def broadcast = listenerManager.getBroadcaster(TestListener)
        services.get(TestListener)

        then:
        0 * _

        when:
        broadcast.something("12")

        then:
        1 * listener.something("12")
        0 * _
    }

    def "does not eagerly create listener manager"() {
        def created = Mock(Runnable)
        def listener = Mock(TestListener)
        def services = new DefaultServiceRegistry()

        when:
        services.addProvider(new Object() {
            DefaultListenerManager createListenerManager() {
                created.run()
                return listenerManager
            }
        })
        services.add(listener)

        then:
        0 * _

        when:
        services.get(ListenerManager)

        then:
        1 * created.run()
        0 * _
    }

    def "registers listeners that are registered before listener manager"() {
        given:
        def created = Mock(Runnable)
        def listener = Mock(TestListener)
        def services = new DefaultServiceRegistry()

        when:
        services.addProvider(new Object() {
            TestListener createTestListener() {
                created.run()
                return listener
            }
        })
        services.addProvider(new Object() {
            DefaultListenerManager createListenerManager() {
                return listenerManager
            }
        })
        def broadcast = services.get(ListenerManager).getBroadcaster(TestListener)

        then:
        0 * _

        when:
        broadcast.something("12")

        then:
        1 * created.run()
        1 * listener.something("12")
        0 * _
    }

    def "fails when listener manager is not declared as annotation handler"() {
        given:
        def services = new DefaultServiceRegistry()

        when:
        services.add(ListenerManager, new DefaultListenerManager(Scopes.BuildTree))

        then:
        def e = thrown(IllegalStateException)
        e.message == 'Service ListenerManager with implementation DefaultListenerManager implements AnnotatedServiceLifecycleHandler but is not declared as a service of this type. This service is declared as having type ListenerManager.'
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
        e.message == 'Service ListenerManager at DefaultListenerManagerInServiceRegistryTest$.createListenerManager() implements AnnotatedServiceLifecycleHandler but is not declared as a service of this type. This service is declared as having type ListenerManager.'
    }

    def "fails when listener instance is not declared as listener type"() {
        def listener = Mock(SubListener)
        services.addProvider(new Object() {
            Runnable createListener() {
                return listener
            }
        })

        when:
        services.get(Runnable)

        then:
        def e = thrown(IllegalStateException)
        e.message == 'Service Runnable at DefaultListenerManagerInServiceRegistryTest$.createListener() is annotated with @StatefulListener but is not declared as a service with this annotation. This service is declared as having type Runnable.'
    }

    @EventScope(Scopes.BuildTree)
    @StatefulListener
    interface TestListener {
        void something(String param)
    }

    abstract static class SubListener implements TestListener, Runnable {
    }
}
