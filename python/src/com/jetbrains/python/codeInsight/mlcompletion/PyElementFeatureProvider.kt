// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jetbrains.python.codeInsight.mlcompletion

import com.intellij.codeInsight.completion.CompletionLocation
import com.intellij.codeInsight.completion.ml.ContextFeatures
import com.intellij.codeInsight.completion.ml.ElementFeatureProvider
import com.intellij.codeInsight.completion.ml.MLFeatureValue
import com.intellij.codeInsight.lookup.LookupElement

class PyElementFeatureProvider : ElementFeatureProvider {
  override fun getName(): String = "python"

  override fun calculateFeatures(element: LookupElement,
                                 location: CompletionLocation,
                                 contextFeatures: ContextFeatures): Map<String, MLFeatureValue> {
    val result = HashMap<String, MLFeatureValue>()

    val lookupString = element.lookupString
    val locationPsi = location.completionParameters.position

    PyCompletionFeatures.getPyLookupElementInfo(element)?.let { info ->
      result["kind"] = MLFeatureValue.categorical(info.kind)
      result["is_builtins"] = MLFeatureValue.binary(info.isBuiltins)
      PyCompletionFeatures.getNumberOfOccurrencesInScope(info.kind, locationPsi, lookupString)?.let {
        result["number_of_occurrences_in_scope"] = MLFeatureValue.numerical(it)
      }
      PyCompletionFeatures.getBuiltinPopularityFeature(lookupString, info.isBuiltins)?.let {
        result["builtin_popularity"] = MLFeatureValue.numerical(it)
      }
    }

    PyCompletionFeatures.getKeywordId(lookupString)?.let {
      result["keyword_id"] = MLFeatureValue.numerical(it)
    }

    result["is_dict_key"] = MLFeatureValue.binary(PyCompletionFeatures.isDictKey(element))
    result["is_the_same_file"] = MLFeatureValue.binary(PyCompletionFeatures.isTheSameFile(element, location))
    result["is_takes_parameter_self"] = MLFeatureValue.binary(PyCompletionFeatures.isTakesParameterSelf(element))
    result["underscore_type"] = MLFeatureValue.categorical(PyCompletionFeatures.getElementNameUnderscoreType(lookupString))
    result["number_of_tokens"] = MLFeatureValue.numerical(PyNamesMatchingMlCompletionFeatures.getNumTokensFeature(lookupString))
    result["element_is_py_file"] = MLFeatureValue.binary(PyCompletionFeatures.isPsiElementIsPyFile(element))
    result["element_is_psi_directory"] = MLFeatureValue.binary(PyCompletionFeatures.isPsiElementIsPsiDirectory(element))

    PyCompletionFeatures.getElementModuleCompletionFeatures(element)?.let { with(it) {
        result["element_module_is_std_lib"] = MLFeatureValue.binary(isFromStdLib)
        result["can_find_element_module"] = MLFeatureValue.binary(canFindModule)
      }
    }

    PyImportCompletionFeatures.getImportPopularityFeature(locationPsi, lookupString)?.let {
      result["import_popularity"] = MLFeatureValue.numerical(it)
    }

    PyImportCompletionFeatures.getElementImportPathFeatures(element, location)?.let { with (it) {
      result["is_imported"] = MLFeatureValue.binary(isImported)
      result["num_components_in_import_path"] = MLFeatureValue.numerical(numComponents)
      result["num_private_components_in_import_path"] = MLFeatureValue.numerical(numPrivateComponents)
    }}

    PyNamesMatchingMlCompletionFeatures.getPyFunClassFileBodyMatchingFeatures(contextFeatures, element.lookupString)?.let { with(it) {
      result["scope_num_names"] = MLFeatureValue.numerical(numScopeNames)
      result["scope_num_different_names"] = MLFeatureValue.numerical(numScopeDifferentNames)
      result["scope_num_matches"] = MLFeatureValue.numerical(sumMatches)
      result["scope_num_tokens_matches"] = MLFeatureValue.numerical(sumTokensMatches)
    }}

    PyNamesMatchingMlCompletionFeatures.getPySameLineMatchingFeatures(contextFeatures, element.lookupString)?.let { with(it) {
      result["same_line_num_names"] = MLFeatureValue.numerical(numScopeNames)
      result["same_line_num_different_names"] = MLFeatureValue.numerical(numScopeDifferentNames)
      result["same_line_num_matches"] = MLFeatureValue.numerical(sumMatches)
      result["same_line_num_tokens_matches"] = MLFeatureValue.numerical(sumTokensMatches)
    }}

    PyNamesMatchingMlCompletionFeatures.getMatchingWithReceiverFeatures(contextFeatures, element)?.let { with(it) {
      result["receiver_name_matches"] = MLFeatureValue.binary(matchesWithReceiver)
      result["receiver_num_matched_tokens"] = MLFeatureValue.numerical(numMatchedTokens)
      result["receiver_tokens_num"] = MLFeatureValue.numerical(receiverTokensNum)
    }}

    return result
  }
}