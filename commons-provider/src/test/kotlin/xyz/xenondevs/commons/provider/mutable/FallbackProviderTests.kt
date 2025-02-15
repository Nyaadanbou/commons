package xyz.xenondevs.commons.provider.mutable

import org.junit.jupiter.api.Test
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.observed
import xyz.xenondevs.commons.provider.orElse
import xyz.xenondevs.commons.provider.orElseLazily
import xyz.xenondevs.commons.provider.orElseNew
import xyz.xenondevs.commons.provider.provider
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FallbackProviderTests {
    
    @Test
    fun testOrElse() {
        val provider = mutableProvider<Int?>(null)
        val orElse = provider.orElse(-1)
        
        assertEquals(null, provider.get())
        assertEquals(-1, orElse.get())
        
        provider.set(1)
        
        assertEquals(1, provider.get())
        assertEquals(1, orElse.get())
        
        orElse.set(2)
        
        assertEquals(2, provider.get())
        assertEquals(2, orElse.get())
        
        orElse.set(-1)
        
        assertEquals(null, provider.get())
        assertEquals(-1, orElse.get())
    }
    
    @Test
    fun testOrElseProvider() {
        val provider = mutableProvider<Int?>(null)
        val mutFallback = mutableProvider(1)
        val fallback: Provider<Int> = mutFallback
        val orElse = provider.orElse(fallback)
        
        assertEquals(null, provider.get())
        assertEquals(1, fallback.get())
        assertEquals(1, orElse.get())
        
        mutFallback.set(2)
        
        assertEquals(null, provider.get())
        assertEquals(2, fallback.get())
        assertEquals(2, orElse.get())
        
        orElse.set(3)
        
        assertEquals(3, provider.get())
        assertEquals(2, fallback.get())
        assertEquals(3, orElse.get())
    }
    
    @Test
    fun testOrElseNullableProvider() {
        val provider = mutableProvider<Int?>(null)
        var fallback: Provider<Int>? = provider(10)
        val orElse1 = provider.orElse(fallback)
        fallback = null
        val orElse2 = provider.orElse(fallback as Provider<Int>?)
        
        assertEquals(null, provider.get())
        assertEquals(10, orElse1.get())
        assertEquals(null, orElse2.get())
        assertTrue(provider === orElse2)
        
        provider.set(1)
        
        assertEquals(1, provider.get())
        assertEquals(1, orElse1.get())
        assertEquals(1, orElse2.get())
    }
    
    @Test
    fun testOrElseLazily() {
        var lazyCalled = false
        
        val provider = mutableProvider<Int?>(0)
        val orElse = provider.orElseLazily {
            lazyCalled = true
            1
        }
        
        orElse.get()
        assertEquals(false, lazyCalled)
        provider.set(null)
        assertEquals(false, lazyCalled)
        orElse.get()
        assertEquals(true, lazyCalled)
    }
    
    @Test
    fun testOrElseNew() {
        val provider = mutableProvider<MutableSet<Int>?>(null)
        val orElse = provider.orElseNew { mutableSetOf() }.observed()
        
        val set = orElse.get()
        set += 1
        assertEquals(setOf(1), orElse.get())
        assertEquals<Set<Int>?>(setOf(1), provider.get())
        
        set -= 1
        assertEquals(emptySet(), orElse.get())
        assertEquals(null, provider.get())
    }
    
}