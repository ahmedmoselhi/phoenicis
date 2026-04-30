/*
 * Copyright (C) 2015-2017 PÂRIS Quentin[cite: 3]
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.[cite: 3]
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.[cite: 3]
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.[cite: 3]
 */

package org.phoenicis.repository.types;[cite: 3]

import org.apache.commons.lang.builder.EqualsBuilder;[cite: 3]
import org.apache.commons.lang.builder.HashCodeBuilder;[cite: 3]
import org.phoenicis.repository.dto.CategoryDTO;
import org.phoenicis.repository.dto.RepositoryDTO;[cite: 3]
import org.phoenicis.repository.dto.ScriptDTO;[cite: 3]

import java.util.List;[cite: 3]
import java.util.concurrent.ExecutorService;[cite: 3]
import java.util.function.Consumer;[cite: 3]

public class BackgroundRepository implements Repository {[cite: 3]
    private final Repository delegatedRepository;[cite: 3]
    private final ExecutorService executorService;[cite: 3]

    public BackgroundRepository(Repository delegatedRepository, ExecutorService executorService) {[cite: 3]
        this.delegatedRepository = delegatedRepository;[cite: 3]
        this.executorService = executorService;[cite: 3]
    }

    @Override
    public RepositoryDTO fetchInstallableApplications() {[cite: 3]
        throw new UnsupportedOperationException("The background apps manager is asynchronous");[cite: 3]
    }

    @Override
    public List<CategoryDTO> fetchCategories() {
        throw new UnsupportedOperationException("The background apps manager is asynchronous");
    }

    @Override
    public RepositoryDTO fetchInstallableApplications(CategoryDTO category) {
        throw new UnsupportedOperationException("The background apps manager is asynchronous");
    }

    @Override
    public void onDelete() {[cite: 3]
        this.delegatedRepository.onDelete();[cite: 3]
    }

    @Override
    public void fetchInstallableApplications(Consumer<RepositoryDTO> callback, Consumer<Exception> errorCallback) {[cite: 3]
        executorService.submit(() -> delegatedRepository.fetchInstallableApplications(callback, errorCallback));[cite: 3]
    }

    @Override
    public void fetchCategories(Consumer<List<CategoryDTO>> callback, Consumer<Exception> errorCallback) {
        executorService.submit(() -> delegatedRepository.fetchCategories(callback, errorCallback));
    }

    @Override
    public void fetchInstallableApplications(CategoryDTO category, Consumer<RepositoryDTO> callback, Consumer<Exception> errorCallback) {
        executorService.submit(() -> delegatedRepository.fetchInstallableApplications(category, callback, errorCallback));
    }

    @Override
    public void getScript(List<String> path, Consumer<ScriptDTO> callback, Consumer<Exception> errorCallback) {[cite: 3]
        executorService.submit(() -> delegatedRepository.getScript(path, callback, errorCallback));[cite: 3]
    }

    @Override
    public boolean equals(Object o) {[cite: 3]
        if (this == o) {[cite: 3]
            return true;[cite: 3]
        }

        if (o == null || getClass() != o.getClass()) {[cite: 3]
            return false;[cite: 3]
        }

        BackgroundRepository that = (BackgroundRepository) o;[cite: 3]

        return new EqualsBuilder()[cite: 3]
                .append(delegatedRepository, that.delegatedRepository)[cite: 3]
                .isEquals();[cite: 3]
    }

    @Override
    public int hashCode() {[cite: 3]
        return new HashCodeBuilder()[cite: 3]
                .append(delegatedRepository)[cite: 3]
                .toHashCode();[cite: 3]
    }

    public static class Factory {[cite: 3]
        public BackgroundRepository createInstance(Repository delegatedRepository, ExecutorService executorService) {[cite: 3]
            return new BackgroundRepository(delegatedRepository, executorService);[cite: 3]
        }
    }
}
