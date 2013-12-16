
/*
import static org.mockito.Mockito.*



// A one element list behavior
scenario "stubbing in mockito works", {
  given "a mocked object", {
    mockedList = mock(LinkedList.class)
  }

  when "it is stubbed", {
    when(mockedList.get(0)).thenReturn("first")
    when(mockedList.get(1)).thenThrow(new RuntimeException())
  }

  then "verifications should work", {
    mockedList.get(0).shouldBe "first"
    ensureThrows(IOException) {
      mockedList.get(1)
    }
    mockedList.get(9).shouldBe null
  }
}

*/