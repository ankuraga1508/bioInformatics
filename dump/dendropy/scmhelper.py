#!/usr/bin/env python
import sys
import copy
import logging

from dendropy.utility.messaging import get_logger
_LOG = get_logger(__name__)
from dendropy import DataSet, TaxonSet
from dendropy.scm import inplace_strict_consensus_merge
from optparse import OptionParser

def exec_scm(input, output):
    trees = []
    taxon_set = TaxonSet()
    dataset = DataSet(taxon_set=taxon_set)
    
    fo = open(input, "rU")
    dataset.read(stream=fo, schema="NEWICK")
    for tl in dataset.tree_lists:
        trees.extend(tl)
    
    o = inplace_strict_consensus_merge(trees, False)
    f = open(output, 'w')
    f.write("%s;\n" % str(o))