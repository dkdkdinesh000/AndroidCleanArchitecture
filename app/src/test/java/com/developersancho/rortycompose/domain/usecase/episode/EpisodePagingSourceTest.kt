package com.developersancho.rortycompose.domain.usecase.episode

import androidx.paging.PagingSource
import com.developersancho.rortycompose.data.model.dto.episode.EpisodeDto
import com.developersancho.rortycompose.data.model.dto.episode.toEpisodeDtoList
import com.developersancho.rortycompose.data.repository.episode.EpisodeRepository
import com.developersancho.rortycompose.domain.mockdata.MockData
import com.developersancho.testutils.MockkUnitTest
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class EpisodePagingSourceTest : MockkUnitTest() {
    @RelaxedMockK
    lateinit var repository: EpisodeRepository

    @InjectMockKs
    lateinit var options: HashMap<String, String>

    @InjectMockKs
    lateinit var pagingSource: EpisodePagingSource

    @Test
    fun `paging source prepend - success`() = runTest {
        // Arrange (Given)
        val response = MockData.getEpisodeResponse()
        coEvery { repository.getEpisodeList(any(), any()) } returns response

        // Act (When)
        val expectedResult = PagingSource.LoadResult.Page(
            data = response.results.orEmpty().toEpisodeDtoList(),
            prevKey = -1,
            nextKey = null
        )

        // Assert (Then)
        Assert.assertEquals(
            expectedResult,
            pagingSource.load(
                PagingSource.LoadParams.Prepend(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `paging source append - success`() = runTest {
        // Arrange (Given)
        val response = MockData.getEpisodeResponse()
        coEvery { repository.getEpisodeList(any(), any()) } returns response

        // Act (When)
        val expectedResult = PagingSource.LoadResult.Page(
            data = response.results.orEmpty().toEpisodeDtoList(),
            prevKey = 2,
            nextKey = null
        )

        // Assert (Then)
        Assert.assertEquals(
            expectedResult,
            pagingSource.load(
                PagingSource.LoadParams.Append(
                    key = 3,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `paging source refresh - success`() = runTest {
        // Arrange (Given)
        val response = MockData.getEpisodeResponse()
        coEvery { repository.getEpisodeList(any(), any()) } returns response

        // Act (When)
        val expectedResult = PagingSource.LoadResult.Page(
            data = response.results.orEmpty().toEpisodeDtoList(),
            prevKey = 2,
            nextKey = null
        )

        // Assert (Then)
        Assert.assertEquals(
            expectedResult,
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 3,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `paging source load - failure - received null`() = runTest {
        // Arrange (Given)
        val error = NullPointerException()
        coEvery { repository.getEpisodeList(any(), any()) } throws error

        // Act (When)
        val expectedResult = PagingSource.LoadResult.Error<Int, EpisodeDto>(error)

        // Assert (Then)
        var exceptionThrown: Boolean = false
        try {
            Assert.assertEquals(
                expectedResult.toString(),
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                ).toString()
            )
        } catch (exception: NullPointerException) {
            // Maybe put some assertions on the exception here.
            exceptionThrown = true
        }
        Assert.assertTrue(exceptionThrown)
    }

    @Test
    fun `paging source load - failure - received http error`() = runTest {
        // Arrange (Given)
        val error = RuntimeException("404", Throwable())
        coEvery { repository.getEpisodeList(any(), any()) }.throws(error)

        // Act (When)
        val expectedResult = PagingSource.LoadResult.Error<Int, EpisodeDto>(error)
        // Assert (Then)
        var exceptionThrown: Boolean = false
        try {
            Assert.assertEquals(
                expectedResult.toString(),
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                ).toString()
            )
        } catch (exception: RuntimeException) {
            // Maybe put some assertions on the exception here.
            exceptionThrown = true
        }
        Assert.assertTrue(exceptionThrown)
    }
}