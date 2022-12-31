package com.bkahlert.aws.cdk

import software.amazon.awscdk.Names
import software.constructs.IConstruct
import software.constructs.Node

/**
 * Returns a CloudFormation-compatible unique identifier for a construct based on its path.
 * @see Names.uniqueId
 */
val IConstruct.uid: String get() = Names.uniqueId(this)

/**
 * Returns an opaque tree-unique address for this construct.
 * @see Node.getAddr
 */
val IConstruct.addr: String get() = node.addr
