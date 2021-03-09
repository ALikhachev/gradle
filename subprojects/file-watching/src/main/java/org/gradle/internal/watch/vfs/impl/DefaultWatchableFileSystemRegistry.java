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

package org.gradle.internal.watch.vfs.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.rubygrapefruit.platform.Native;
import net.rubygrapefruit.platform.file.FileSystemInfo;
import net.rubygrapefruit.platform.file.FileSystems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DefaultWatchableFileSystemRegistry implements WatchableFileSystemRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWatchableFileSystemRegistry.class);

    private static final ImmutableSet<String> SUPPORTED_FILE_SYSTEM_TYPES = ImmutableSet.of(
        // APFS on macOS
        "apfs",
        // HFS and HFS+ on macOS
        "hfs",
        "ext3",
        "ext4",
        "btrfs",
        // NTFS on macOS
        "ntfs",
        // NTFS on Windows
        "NTFS",
        // FAT32 on macOS
        "msdos",
        // FAT32 on Windows
        "FAT32",
        // exFAT on macOS
        "exfat",
        // exFAT on Windows
        "exFAT",
        // VirtualBox FS
        "vboxsf"
    );

    private final ImmutableMap<String, Boolean> fileSystemRoots;

    public static WatchableFileSystemRegistry create() {
        return new DefaultWatchableFileSystemRegistry(Native.get(FileSystems.class).getFileSystems());
    }

    @VisibleForTesting
    DefaultWatchableFileSystemRegistry(List<FileSystemInfo> fileSystems) {
        ImmutableMap.Builder<String, Boolean> builder = ImmutableMap.builder();
        fileSystems.stream()
            // Sort by longest path first so we always match most the specific location in case locations are nested
            .sorted(Comparator.comparingInt(fileSystem -> -fileSystem.getMountPoint().getAbsolutePath().length()))
            .forEach(fileSystem -> {
                boolean supported = isSupported(fileSystem);
                String prefix = toAbsolutePathPrefix(fileSystem.getMountPoint());
                LOGGER.info("Detected {} {}: {} from {} (remote: {}, case-sensitive: {}, case-preserving: {})",
                    supported ? "supported" : "unsupported",
                    fileSystem.getFileSystemType(),
                    prefix,
                    fileSystem.getDeviceName(),
                    fileSystem.isRemote(),
                    fileSystem.isCaseSensitive(),
                    fileSystem.isCasePreserving());
                builder.put(prefix, supported);
            });
        this.fileSystemRoots = builder.build();
    }

    private static boolean isSupported(FileSystemInfo fileSystem) {
        // We don't support network file systems
        if (fileSystem.isRemote()) {
            return false;
        }
        return SUPPORTED_FILE_SYSTEM_TYPES.contains(fileSystem.getFileSystemType());
    }

    @Override
    public boolean isWatchingSupported(File path) {
        String prefix = toAbsolutePathPrefix(path);
        for (Map.Entry<String, Boolean> entry : fileSystemRoots.entrySet()) {
            if (prefix.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return false;
    }

    private static String toAbsolutePathPrefix(File path) {
        String absolutePath = path.getAbsolutePath();
        return absolutePath.equals(File.separator)
            ? absolutePath
            : absolutePath + File.separatorChar;
    }
}
