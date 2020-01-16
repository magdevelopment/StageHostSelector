package com.magdv.stagehostselector.common

import kotlin.properties.Delegates

class ObservableCurrentHostUrl(listener: CurrentHostUrlChangeListener) {

    var currentHostUrl: String? by Delegates.observable<String?>(
        initialValue = null,
        onChange = { _, _, new ->
            listener.onValueChanged(new)
        }
    )
}