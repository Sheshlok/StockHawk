package com.sam_chordas.android.stockhawk.data;

import com.sam_chordas.android.stockhawk.data.networkingAPI.NetworkingModule;
import com.sam_chordas.android.stockhawk.data.provider.ProviderModule;
import com.sam_chordas.android.stockhawk.data.repository.RepositoryModule;

import dagger.Module;

/**
 * Created by sheshloksamal on 20/07/16.
 *
 */

@Module(
        includes = {
                NetworkingModule.class,
                RepositoryModule.class,
                ProviderModule.class
        }
)
public class DataModule {}
