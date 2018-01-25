package com.shazam.fork.model

import com.agoda.fork.stat.TestMetric
import com.google.common.base.Objects

import org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString
import org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE

data class TestCaseEvent internal constructor(val testMethod: String,
                                              val testClass: String,
                                              val isIgnored: Boolean,
                                              val permissionsToRevoke: List<String>,
                                              val properties: Map<String, String>,
                                              val testMetric: TestMetric) {

    override fun hashCode(): Int {
        return Objects.hashCode(this.testMethod, this.testClass)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as? TestCaseEvent?
        return Objects.equal(this.testMethod, other?.testMethod) && Objects.equal(this.testClass, other?.testClass)
    }

    override fun toString(): String {
        return reflectionToString(this, SIMPLE_STYLE)
    }
}
