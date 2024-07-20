package xyz.xenondevs.commons.provider.mutable

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MappingProviderTests {
    
    @Test
    fun testMap() {
        val provider = mutableProvider(1)
        val mappedProvider = provider.map({ it + 1 }, { it - 1 })
        
        assertEquals(1, provider.get())
        assertEquals(2, mappedProvider.get())
        
        provider.set(2)
        
        assertEquals(2, provider.get())
        assertEquals(3, mappedProvider.get())
        
        mappedProvider.set(4)
        
        assertEquals(3, provider.get())
        assertEquals(4, mappedProvider.get())
    }
    
    @Test
    fun testMapNonNull() {
        val provider = mutableProvider<Int?>(null)
        val mappedProvider = provider.mapNonNull({ it + 1 }, { it - 1 })
        
        assertEquals(null, provider.get())
        assertEquals(null, mappedProvider.get())
        
        provider.set(2)
        
        assertEquals(2, provider.get())
        assertEquals(3, mappedProvider.get())
        
        mappedProvider.set(4)
        
        assertEquals(3, provider.get())
        assertEquals(4, mappedProvider.get())
        
        mappedProvider.set(null)
        
        assertEquals(null, provider.get())
        assertEquals(null, mappedProvider.get())
    }
    
}