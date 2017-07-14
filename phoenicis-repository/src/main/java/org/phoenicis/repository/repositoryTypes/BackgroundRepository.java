/*
 * Copyright (C) 2015-2017 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.phoenicis.repository.repositoryTypes;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.phoenicis.repository.dto.RepositoryDTO;
import org.phoenicis.repository.dto.ScriptDTO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BackgroundRepository implements Repository {
    private final Repository delegatedRepository;
    private final ExecutorService executorService;

    public BackgroundRepository(Repository delegatedRepository, ExecutorService executorService) {
        this.delegatedRepository = delegatedRepository;
        this.executorService = executorService;
    }

    @Override
    public RepositoryDTO fetchInstallableApplications() {
        throw new UnsupportedOperationException("The background apps manager is asynchronous");
    }

    @Override
    public void onDelete() {
        this.delegatedRepository.onDelete();
    }

    @Override
    public void fetchInstallableApplications(Consumer<RepositoryDTO> callback, Consumer<Exception> errorCallback) {
        executorService.submit(() -> delegatedRepository.fetchInstallableApplications(callback, errorCallback));
    }

    @Override
    public void getScript(List<String> path, Consumer<ScriptDTO> callback, Consumer<Exception> errorCallback) {
        executorService.submit(() -> delegatedRepository.getScript(path, callback, errorCallback));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BackgroundRepository that = (BackgroundRepository) o;

        return new EqualsBuilder()
                .append(delegatedRepository, that.delegatedRepository)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(delegatedRepository)
                .toHashCode();
    }

    public static class Factory {
        public BackgroundRepository createInstance(Repository delegatedRepository, ExecutorService executorService) {
            return new BackgroundRepository(delegatedRepository, executorService);
        }
    }
}
