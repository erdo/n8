package co.early.n8

import co.early.fore.kt.core.coroutine.awaitDefault
import co.early.fore.kt.core.coroutine.launchDefault
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.reflect.typeOf


class NavigationImportExportTest {

    private lateinit var dataDirectory: File

    @Before
    fun setup() {

        Fore.setDelegate(TestDelegateDefault())

        val dataFolder = TemporaryFolder()
        dataFolder.create()
        dataDirectory = dataFolder.newFolder()
    }

    @Test
    fun `when exporting state, serialized representation is correct`() {

        // arrange
        val navigationModel = NavigationModel(
            initialNavigation = backStackOf(
                endNodeOf(NestedTestData.Location.A),
                endNodeOf(NestedTestData.Location.B),
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X1),
                        endNodeOf(NestedTestData.Location.C),
                        endNodeOf(NestedTestData.Location.D),
                        tabsOf(
                            tabHistory = listOf(0, 1),
                            tabHostId = tabHostSpecX12.tabHostId,
                            backStackOf(
                                endNodeOf(NestedTestData.Location.Y1),
                                endNodeOf(NestedTestData.Location.E)
                            ),
                            backStackOf(
                                endNodeOf(NestedTestData.Location.Y2)
                            )
                        )
                    ),
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X1)
                    ),
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X2)
                    )
                ),
                endNodeOf(NestedTestData.Location.C),
            ),
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )

        launchDefault {

            // act
            val serialized = awaitDefault {
                navigationModel.serializeState()
            }

            Fore.e(navigationModel.toString(diagnostics = true))

            val expected = """
            {
              "navigation": [
                "co.early.n8.Navigation.BackStack",
                {
                  "stack": [
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.A"}}],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.B"}}],
                    ["co.early.n8.Navigation.TabHost", {
                      "tabHistory": [0],
                      "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabAbc"},
                      "tabs": [
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.D"}}],
                            ["co.early.n8.Navigation.TabHost", {
                              "tabHistory": [0, 1],
                              "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabX12"},
                              "tabs": [
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y1"}}],
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.E"}}]
                                  ]
                                },
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y2"}}]
                                  ]
                                }
                              ]
                            }]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X2"}}]
                          ]
                        }
                      ]
                    }],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}]
                  ]
                }
              ]
            }
        """.trimIndent()

            // assert
            assertEquals(expected.replace("\\s".toRegex(), ""), serialized)
        }
    }

    @Test
    fun `when importing serialized state, but not setting, navigation state returned is correct`() {

        // arrange
        val navigationModel = NavigationModel<NestedTestData.Location, NestedTestData.TabHost>(
            homeLocation = NestedTestData.Location.A,
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )
        val serialized = """
            {
              "navigation": [
                "co.early.n8.Navigation.BackStack",
                {
                  "stack": [
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.A"}}],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.B"}}],
                    ["co.early.n8.Navigation.TabHost", {
                      "tabHistory": [0],
                      "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabAbc"},
                      "tabs": [
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.D"}}],
                            ["co.early.n8.Navigation.TabHost", {
                              "tabHistory": [0, 1],
                              "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabX12"},
                              "tabs": [
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y1"}}],
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.E"}}]
                                  ]
                                },
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y2"}}]
                                  ]
                                }
                              ]
                            }]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X2"}}]
                          ]
                        }
                      ]
                    }],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}]
                  ]
                }
              ],
              "willBeAddedToHistory": false
            }
        """.trimIndent()

        val expected = NavigationState(
            navigation = backStackOf(
                endNodeOf(NestedTestData.Location.A),
                endNodeOf(NestedTestData.Location.B),
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X1),
                        endNodeOf(NestedTestData.Location.C),
                        endNodeOf(NestedTestData.Location.D),
                        tabsOf(
                            tabHistory = listOf(0, 1),
                            tabHostId = tabHostSpecX12.tabHostId,
                            backStackOf(
                                endNodeOf(NestedTestData.Location.Y1),
                                endNodeOf(NestedTestData.Location.E)
                            ),
                            backStackOf(
                                endNodeOf(NestedTestData.Location.Y2)
                            )
                        )
                    ),
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X1)
                    ),
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X2)
                    )
                ),
                endNodeOf(NestedTestData.Location.C),
            ),
            willBeAddedToHistory = false,
        )

        launchDefault {

            // act
            val deSerializedNav = awaitDefault {
                navigationModel.deSerializeState(serialized, setAsState = false)
            }

            // assert
            assertEquals(expected, deSerializedNav)
            assertEquals(NestedTestData.Location.A, navigationModel.state.currentLocation)
            assertEquals(1, navigationModel.state.backsToExit)
            assertEquals(true, navigationModel.state.willBeAddedToHistory)
        }
    }

    @Test
    fun `when importing serialized state, and setting navigation state is set correctly`() {

        // arrange
        val navigationModel = NavigationModel<NestedTestData.Location, NestedTestData.TabHost>(
            homeLocation = NestedTestData.Location.A,
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )
        val serialized = """
            {
              "navigation": [
                "co.early.n8.Navigation.BackStack",
                {
                  "stack": [
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.A"}}],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.B"}}],
                    ["co.early.n8.Navigation.TabHost", {
                      "tabHistory": [0],
                      "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabAbc"},
                      "tabs": [
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.D"}}],
                            ["co.early.n8.Navigation.TabHost", {
                              "tabHistory": [0, 1],
                              "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabX12"},
                              "tabs": [
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y1"}}],
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.E"}}]
                                  ]
                                },
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y2"}}]
                                  ]
                                }
                              ]
                            }]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X2"}}]
                          ]
                        }
                      ]
                    }],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}]
                  ]
                }
              ],
              "willBeAddedToHistory": false
            }
        """.trimIndent()

        launchDefault {

            // act
            awaitDefault {
                navigationModel.deSerializeState(serialized, setAsState = true)
            }

            // assert
            assertEquals(NestedTestData.Location.C, navigationModel.state.currentLocation)
            assertEquals(9, navigationModel.state.backsToExit)
            assertEquals(false, navigationModel.state.willBeAddedToHistory)
        }
    }


    /**
     * The following are more of a demo of how to do very basic minimisation of the exported serialised state data, it's a little
     * outside the scope of a navigation library IMO, n8 will export / import a serialized version of it's state, but the client
     * can minify / compress / encrypt / urlEncode that serialized representation as they see fit (for sending to other devices)
     */

    // obviously be careful here, it can become very hacky. but we get quite a big bang for our buck by using basic token
    // replacement compared with any other compression technique
    val tokens = mapOf(
        "co.early.n8.Navigation.EndNode" to "EN",
        "co.early.n8.Navigation.TabHost" to "TH",
        "co.early.n8.Navigation.BackStack" to "BS",
        "co.early.n8.NestedTestData.Location.A" to "LA",
        "co.early.n8.NestedTestData.Location.B" to "LB",
        "co.early.n8.NestedTestData.Location.C" to "LC",
        "co.early.n8.NestedTestData.Location.D" to "LD",
        "co.early.n8.NestedTestData.Location.E" to "LE",
        "co.early.n8.NestedTestData.Location.Y1" to "LY1",
        "co.early.n8.NestedTestData.Location.Y2" to "LY2",
        "co.early.n8.NestedTestData.Location.X1" to "LX1",
        "co.early.n8.NestedTestData.Location.X2" to "LX2",
        "co.early.n8.NestedTestData.TabHost.TabAbc" to "TABC",
        "co.early.n8.NestedTestData.TabHost.TabX12" to "TX12",
        "navigation" to "n",
        "willBeAddedToHistory" to "w",
        "tabHistory" to "h",
        "location" to "l",
        "tabs" to "t",
        "type" to "p",
        "stack" to "s",
        "tabHostId" to "i",
    )

    fun String.tokenize(tokens: Map<String, String>): String {
        var result = this
        tokens.forEach { (key, value) ->
            result = result.replace("\"$key\"", "\"$value\"")
        }
        return result
    }

    fun String.deTokenize(tokens: Map<String, String>): String {
        val reversedMap = tokens.entries.associate { it.value to it.key }
        return this.tokenize(reversedMap)
    }

    @Test
    fun `when exporting state, serialized representation can be tokenized`() {

        // arrange
        val navigationModel = NavigationModel(
            initialNavigation = backStackOf(
                endNodeOf(NestedTestData.Location.A),
                endNodeOf(NestedTestData.Location.B),
                tabsOf(
                    tabHistory = listOf(0),
                    tabHostId = tabHostSpecAbc.tabHostId,
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X1),
                        endNodeOf(NestedTestData.Location.C),
                        endNodeOf(NestedTestData.Location.D),
                        tabsOf(
                            tabHistory = listOf(0, 1),
                            tabHostId = tabHostSpecX12.tabHostId,
                            backStackOf(
                                endNodeOf(NestedTestData.Location.Y1),
                                endNodeOf(NestedTestData.Location.E)
                            ),
                            backStackOf(
                                endNodeOf(NestedTestData.Location.Y2)
                            )
                        )
                    ),
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X1)
                    ),
                    backStackOf(
                        endNodeOf(NestedTestData.Location.X2)
                    )
                ),
                endNodeOf(NestedTestData.Location.C),
            ),
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )

        launchDefault {

            // act
            val tokenized = awaitDefault {
                navigationModel.serializeState().tokenize(tokens)
            }

            val expected = """
            {
              "n": [
                "BS",
                {
                  "s": [
                    ["EN", {"l": {"p": "LA"}}],
                    ["EN", {"l": {"p": "LB"}}],
                    ["TH", {
                      "h": [0],
                      "i": {"p": "TABC"},
                      "t": [
                        {
                          "s": [
                            ["EN", {"l": {"p": "LX1"}}],
                            ["EN", {"l": {"p": "LC"}}],
                            ["EN", {"l": {"p": "LD"}}],
                            ["TH", {
                              "h": [0, 1],
                              "i": {"p": "TX12"},
                              "t": [
                                {
                                  "s": [
                                    ["EN", {"l": {"p": "LY1"}}],
                                    ["EN", {"l": {"p": "LE"}}]
                                  ]
                                },
                                {
                                  "s": [
                                    ["EN", {"l": {"p": "LY2"}}]
                                  ]
                                }
                              ]
                            }]
                          ]
                        },
                        {
                          "s": [
                            ["EN", {"l": {"p": "LX1"}}]
                          ]
                        },
                        {
                          "s": [
                            ["EN", {"l": {"p": "LX2"}}]
                          ]
                        }
                      ]
                    }],
                    ["EN", {"l": {"p": "LC"}}]
                  ]
                }
              ]
            }
        """.trimIndent().replace("\\s".toRegex(), "")

            Fore.i(expected)

            // assert
            assertEquals(expected, tokenized)
        }
    }

    @Test
    fun `when importing serialized state, string can be detokenized successfully`() {

        // arrange
        val navigationModel = NavigationModel<NestedTestData.Location, NestedTestData.TabHost>(
            homeLocation = NestedTestData.Location.A,
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )
        val serialized = """
            {
              "n": [
                "BS",
                {
                  "s": [
                    ["EN", {"l": {"p": "LA"}}],
                    ["EN", {"l": {"p": "LB"}}],
                    ["TH", {
                      "h": [0],
                      "i": {"p": "TABC"},
                      "t": [
                        {
                          "s": [
                            ["EN", {"l": {"p": "LX1"}}],
                            ["EN", {"l": {"p": "LC"}}],
                            ["EN", {"l": {"p": "LD"}}],
                            ["TH", {
                              "h": [0, 1],
                              "i": {"p": "TX12"},
                              "t": [
                                {
                                  "s": [
                                    ["EN", {"l": {"p": "LY1"}}],
                                    ["EN", {"l": {"p": "LE"}}]
                                  ]
                                },
                                {
                                  "s": [
                                    ["EN", {"l": {"p": "LY2"}}]
                                  ]
                                }
                              ]
                            }]
                          ]
                        },
                        {
                          "s": [
                            ["EN", {"l": {"p": "LX1"}}]
                          ]
                        },
                        {
                          "s": [
                            ["EN", {"l": {"p": "LX2"}}]
                          ]
                        }
                      ]
                    }],
                    ["EN", {"l": {"p": "LC"}}]
                  ]
                }
              ]
            }
        """.trimIndent().deTokenize(tokens)

        Fore.i(serialized)

        launchDefault {

            // act
            awaitDefault {
                navigationModel.deSerializeState(serialized, setAsState = true)
            }

            // assert
            assertEquals(NestedTestData.Location.C, navigationModel.state.currentLocation)
            assertEquals(9, navigationModel.state.backsToExit)
        }
    }

    @Test
    fun `when deserializing a navigation graph with duplicate tabHostIds, exception is thrown`() {

        // arrange
        var exception: Exception? = null
        val navigationModel = NavigationModel<NestedTestData.Location, NestedTestData.TabHost>(
            homeLocation = NestedTestData.Location.A,
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )
        val serialized = """
            {
              "navigation": [
                "co.early.n8.Navigation.BackStack",
                {
                  "stack": [
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.A"}}],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.B"}}],
                    ["co.early.n8.Navigation.TabHost", {
                      "tabHistory": [0],
                      "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabAbc"},
                      "tabs": [
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}],
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.D"}}],
                            ["co.early.n8.Navigation.TabHost", {
                              "tabHistory": [0, 1],
                              "tabHostId": {"type": "co.early.n8.NestedTestData.TabHost.TabAbc"},
                              "tabs": [
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y1"}}],
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.E"}}]
                                  ]
                                },
                                {
                                  "stack": [
                                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.Y2"}}]
                                  ]
                                }
                              ]
                            }]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X1"}}]
                          ]
                        },
                        {
                          "stack": [
                            ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.X2"}}]
                          ]
                        }
                      ]
                    }],
                    ["co.early.n8.Navigation.EndNode", {"location": {"type": "co.early.n8.NestedTestData.Location.C"}}]
                  ]
                }
              ],
              "willBeAddedToHistory": false
            }
        """.trimIndent()

        launchDefault {
            awaitDefault {
                try {
                    // act
                    navigationModel.deSerializeState(serialized, setAsState = true)
                } catch (e: Exception) {
                    Fore.e(e.message ?: "exception with no message")
                    exception = e
                }
            }

            // assert
            Assert.assertNotEquals(null, exception)
        }
    }

    @Test
    fun `when rewriting with a navigation graph with duplicate tabHostIds, exception is thrown`() {

        // arrange
        var exception: Exception? = null
        val navigationModel = NavigationModel<NestedTestData.Location, NestedTestData.TabHost>(
            homeLocation = NestedTestData.Location.A,
            stateKType = typeOf<NavigationState<NestedTestData.Location, NestedTestData.TabHost>>(),
            dataDirectory = dataDirectory,
        )

        // act
        try {
            navigationModel.reWriteNavigation(
                Navigation.BackStack(
                    stack = listOf(
                        Navigation.TabHost(
                            tabHistory = listOf(0),
                            tabHostId = NestedTestData.TabHost.TabAbc,
                            tabs = listOf(
                                Navigation.BackStack(
                                    stack = listOf(
                                        Navigation.EndNode(NestedTestData.Location.A)
                                    )
                                )
                            )
                        ),
                        Navigation.TabHost(
                            tabHistory = listOf(0),
                            tabHostId = NestedTestData.TabHost.TabAbc,
                            tabs = listOf(
                                Navigation.BackStack(
                                    stack = listOf(
                                        Navigation.EndNode(NestedTestData.Location.A)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        } catch (e: Exception) {
            Fore.e(e.message ?: "exception with no message")
            exception = e
        }

        // assert
        Assert.assertNotEquals(null, exception)
    }

}
