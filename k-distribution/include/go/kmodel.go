package main

import (
	"fmt"
	"strings"
)

// K ... Defines a K entity
type K interface {
	PrettyTreePrint(indent int) string
}

// KSequence ... a sequence of K items
type KSequence struct {
	ks []K
}

// KItem ...
type KItem interface {
}

// KApply ... a type of KItem, TODO: document
type KApply struct {
	Label KLabel
	List  []K
}

// InjectedKLabel ... a type of KItem, TODO: document
type InjectedKLabel struct {
	Label KLabel
}

// KToken ... a type of KItem, TODO: document
type KToken struct {
	Value string
	Sort  Sort
}

// KVariable ... a type of KItem, TODO: document
type KVariable struct {
	Name string
}

// Map ... a type of KItem, TODO: document
type Map struct {
	Sort  Sort
	Label KLabel
	data  map[K]K
}

// Set ... a type of KItem, TODO: document
type Set struct {
	Sort  Sort
	Label KLabel
	data  map[K]bool
}

// List ... a type of KItem, TODO: document
type List struct {
	Sort  Sort
	Label KLabel
	data  []K
}

// Array ... a type of KItem, TODO: document
type Array struct {
	Sort  Sort
	Label KLabel
	data  []K
}

// Int ... a type of KItem, TODO: document
type Int int32

// MInt ... a type of KItem, TODO: document
type MInt int32

// Float ... a type of KItem, TODO: document
type Float float32

// String ... a type of KItem, TODO: document
type String string

// StringBuffer ... a type of KItem, TODO: document
type StringBuffer struct {
}

// Bytes ... a type of KItem, TODO: document
type Bytes []byte

// Bool ... a type of KItem, TODO: document
type Bool bool

// Bottom ... a type of KItem, TODO: document
type Bottom struct {
}

func addIndent(sb *strings.Builder, indent int) {
	for i := 0; i < indent; i++ {
		sb.WriteString("    ")
	}
}

func simplePrint(indent int, str string) string {
	var sb strings.Builder
	addIndent(&sb, indent)
	sb.WriteString(str)
	return sb.String()
}

// PrettyTreePrint ... A tree representation of a K object
func (k KApply) PrettyTreePrint(indent int) string {
	var sb strings.Builder
	addIndent(&sb, indent)
	sb.WriteString("KApply {label:")
	sb.WriteString(k.Label.name())
	sb.WriteString(", list:")
	if len(k.List) == 0 {
		sb.WriteString("[] }")
	} else {
		for _, childk := range k.List {
			sb.WriteRune('\n')
			sb.WriteString(childk.PrettyTreePrint(indent + 1))
		}
		sb.WriteRune('\n')
		addIndent(&sb, indent)
		sb.WriteRune('}')
	}

	return sb.String()
}

// PrettyTreePrint ... A tree representation of a K object
func (k InjectedKLabel) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("InjectedKLabel {label:%s}", k.Label.name()))
}

// PrettyTreePrint ... A tree representation of a K object
func (k KToken) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("KToken {value:%s, sort:%s}", k.Value, k.Sort.name()))
}

// PrettyTreePrint ... A tree representation of a K object
func (k KVariable) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("KVariable {name:%s}", k.Name))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Map) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Map {sort:%s, label:%s}", k.Sort.name(), k.Label.name()))
}

// PrettyTreePrint ... A tree representation of a K object
func (k List) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("List {sort:%s, label:%s}", k.Sort.name(), k.Label.name()))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Set) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Set {sort:%s, label:%s}", k.Sort.name(), k.Label.name()))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Array) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Array {sort:%s, label:%s}", k.Sort.name(), k.Label.name()))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Int) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Int (%d)", k))
}

// PrettyTreePrint ... A tree representation of a K object
func (k MInt) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("MInt (%d)", k))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Float) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Float (%f)", k))
}

// PrettyTreePrint ... A tree representation of a K object
func (k String) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("String (%s)", k))
}

// PrettyTreePrint ... A tree representation of a K object
func (k StringBuffer) PrettyTreePrint(indent int) string {
	return simplePrint(indent, "StringBuffer [not yet implemented]")
}

// PrettyTreePrint ... A tree representation of a K object
func (k Bytes) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Bytes (%b)", k))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Bool) PrettyTreePrint(indent int) string {
	return simplePrint(indent, fmt.Sprintf("Bool (%t)", k))
}

// PrettyTreePrint ... A tree representation of a K object
func (k Bottom) PrettyTreePrint(indent int) string {
	return simplePrint(indent, "Bottom")
}

// PrettyTreePrint ... A tree representation of a K object
func (k KSequence) PrettyTreePrint(indent int) string {
	var sb strings.Builder
	addIndent(&sb, indent)
	sb.WriteString("KSequence {")
	if len(k.ks) == 0 {
		sb.WriteString(" <empty> }")
	} else {
		for i, childk := range k.ks {
			sb.WriteString("\n")
			sb.WriteString(childk.PrettyTreePrint(indent + 1))
			if i < len(k.ks)-1 {
				sb.WriteString(" ~>")
			}
		}
		sb.WriteRune('\n')
		addIndent(&sb, indent)
		sb.WriteRune('}')
	}

	return sb.String()
}

func (k KSequence) isEmpty() bool {
	return len(k.ks) == 0
}
